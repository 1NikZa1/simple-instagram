package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.dto.CommunityPostDTO;
import com.nikza.socialnetwork.dto.PostDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.ImageModel;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.exceptions.PostNotFoundException;
import com.nikza.socialnetwork.repository.CommunityRepository;
import com.nikza.socialnetwork.repository.ImageRepository;
import com.nikza.socialnetwork.repository.PostRepository;
import com.nikza.socialnetwork.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    public static final Logger LOG = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final CommunityRepository communityRepository;


    @Autowired
    public PostService(PostRepository postRepository, UserRepository userRepository, ImageRepository imageRepository, CommunityRepository communityRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.communityRepository = communityRepository;
    }

    public Post createPostToUser(PostDTO postDTO, Principal principal) {
        User user = getUserByPrincipal(principal);
        Post post = new Post();
        post.setUser(user);
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);

        LOG.info("saving Post for User: {}", user.getEmail());
        return postRepository.save(post);
    }

    public Post createPostToGroup(CommunityPostDTO postDTO, Long communityId) {
        Post post = new Post();
        post.setCommunity(communityRepository.findById(communityId).orElse(null));
        post.setCaption(postDTO.getCaption());
        post.setLocation(postDTO.getLocation());
        post.setTitle(postDTO.getTitle());
        post.setLikes(0);

        LOG.info("saving Post for Group with id: {}", communityId);
        return postRepository.save(post);
    }

    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreatedDateDesc();
    }

    public Post getPostById(Long postId, Principal principal) {
        User user = getUserByPrincipal(principal);
        return postRepository.findPostByIdAndUser(postId, user)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found for User " + user.getEmail()));
    }

    public List<Post> getAllPostsForUser(Principal principal) {
        User user = getUserByPrincipal(principal);
        return postRepository.findAllByUserOrderByCreatedDate(user);
    }

    public List<Post> getAllPostsFromFollowedCommunities(Principal principal) {
        User user = getUserByPrincipal(principal);
        List<Community> communities = communityRepository.findAllByUsers_id(user.getId());
        return communities.stream().flatMap(community -> postRepository.findAllByCommunityOrderByCreatedDate(community).stream()).sorted((a, b) -> {
            if (a.getCreatedDate().isBefore(b.getCreatedDate())) {
                return 1;
            } else if (a.getCreatedDate().isAfter(b.getCreatedDate())) {
                return -1;
            } else {
                return 0;
            }
        }).collect(Collectors.toList());
    }

    public List<Post> getAllPostsForCommunity(Long communityId) {
        return postRepository.findAllByCommunityOrderByCreatedDate(communityRepository.getById(communityId));
    }

    public Post likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post cannot be found "));

        Optional<String> userLiked = post.getLikedUsers()
                .stream()
                .filter(u -> u.equals(username))
                .findAny();

        if (userLiked.isPresent()) {
            post.setLikes(post.getLikes() - 1);
            post.getLikedUsers().remove(username);
        } else {
            post.setLikes(post.getLikes() + 1);
            post.getLikedUsers().add(username);
        }
        return postRepository.save(post);
    }

    public void deletePost(Long postId, Principal principal) {
        Post post = getPostById(postId, principal);
        Optional<ImageModel> imageModel = imageRepository.findByPostId(post.getId());
        postRepository.delete(post);
        imageModel.ifPresent(imageRepository::delete);
    }

    public User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }
}
