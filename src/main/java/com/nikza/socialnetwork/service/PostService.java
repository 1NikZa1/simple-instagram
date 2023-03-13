package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.dto.CommunityPostDTO;
import com.nikza.socialnetwork.dto.PostDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.ImageModel;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.exceptions.CommunityNotFoundException;
import com.nikza.socialnetwork.exceptions.NoAccessException;
import com.nikza.socialnetwork.exceptions.PostNotFoundException;
import com.nikza.socialnetwork.repository.CommunityRepository;
import com.nikza.socialnetwork.repository.ImageRepository;
import com.nikza.socialnetwork.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final CommunityRepository communityRepository;
    private final UserService userService;

    @Autowired
    public PostService(PostRepository postRepository, ImageRepository imageRepository, CommunityRepository communityRepository, UserService userService) {
        this.postRepository = postRepository;
        this.imageRepository = imageRepository;
        this.communityRepository = communityRepository;
        this.userService = userService;
    }

    public Post createPostToUser(PostDTO postDTO, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Post post = Post.builder()
                .user(user)
                .caption(postDTO.getCaption())
                .location(postDTO.getLocation())
                .title(postDTO.getTitle())
                .likes(0)
                .build();

        LOG.info("Saving Post for User: {}", user.getId());
        return postRepository.save(post);
    }

    public Post createPostToCommunity(CommunityPostDTO postDTO, Long communityId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + communityId));

        if (!user.getId().equals(community.getCreator().getId()))
            throw new NoAccessException("User " + user.getId() + " is not a Community creator");

        Post post = Post.builder()
                .community(community)
                .caption(postDTO.getCaption())
                .location(postDTO.getLocation())
                .title(postDTO.getTitle())
                .likes(0)
                .build();

        LOG.info("Saving Post for Group: {}", communityId);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long postId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        return postRepository.findPostByIdAndUser(postId, user)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for User " + user.getId()));
    }

    public List<Post> getAllPostsForUser(Principal principal) {
        User user = userService.getCurrentUser(principal);
        return postRepository.findAllByUserOrderByCreatedDate(user);
    }

    public List<Post> getAllPostsForUserById(Long userId) {
        return postRepository.findAllByUser_idOrderByCreatedDate(userId);
    }

    public List<Post> getAllPostsForUserByUsername(String username) {
        return postRepository.findAllByUser_usernameOrderByCreatedDate(username);
    }

    public List<Post> getAllPostsFromFollowedCommunities(Principal principal) {
        User user = userService.getCurrentUser(principal);
        List<Community> communities = communityRepository.findAllByUsers_id(user.getId());

        return communities.stream()
                .flatMap(community -> postRepository.findAllByCommunityOrderByCreatedDate(community).stream())
                .sorted(Comparator.comparing(Post::getCreatedDate))
                .toList();
    }

    public List<Post> getAllPostsForCommunity(Long communityId) {
        return postRepository.findAllByCommunityOrderByCreatedDate(communityRepository.getReferenceById(communityId));
    }

    public Post likePost(Long postId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post " + postId + " not found"));

        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(user.getUsername()))
                .findAny();

        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(user.getUsername());
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(user.getUsername());
        }
        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal) {
        Post post = getPostById(postId, principal);
        Optional<ImageModel> imageModel = imageRepository.findByPostId(post.getId());
        postRepository.delete(post);
        imageModel.ifPresent(imageRepository::delete);
        LOG.info("Post {} was deleted", postId);
    }
}
