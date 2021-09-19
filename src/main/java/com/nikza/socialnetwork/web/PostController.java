package com.nikza.socialnetwork.web;

import com.nikza.socialnetwork.dto.CommunityPostDTO;
import com.nikza.socialnetwork.dto.PostDTO;
import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.facade.PostFacade;
import com.nikza.socialnetwork.payload.response.MessageResponse;
import com.nikza.socialnetwork.service.CommunityService;
import com.nikza.socialnetwork.service.PostService;
import com.nikza.socialnetwork.validations.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/post")
@CrossOrigin
public class PostController {

    @Autowired
    private PostService postService;
    @Autowired
    private PostFacade postFacade;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private ResponseErrorValidation responseErrorValidation;

    @PostMapping("/create")
    public ResponseEntity<Object> createPost(@Valid @RequestBody PostDTO postDTO,
                                             BindingResult bindingResult,
                                             Principal principal) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) return errors;
        Post post = postService.createPostToUser(postDTO, principal);
        PostDTO createdPost = postFacade.postToPostDTO(post);

        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @PostMapping("/{communityId}/create")
    public ResponseEntity<Object> createPostForCommunity(@Valid @RequestBody CommunityPostDTO postDTO,
                                                         BindingResult bindingResult,
                                                         Principal principal,
                                                         @PathVariable("communityId") String communityId) {
        ResponseEntity<Object> errors = responseErrorValidation.mapValidationService(bindingResult);

        if (!ObjectUtils.isEmpty(errors)) return errors;

        User user = postService.getUserByPrincipal(principal);
        Community community = communityService.getCommunityById(Long.parseLong(communityId));
        CommunityPostDTO createdPost = null;

        if (user.getId().equals(community.getCreator().getId())){
            Post post = postService.createPostToGroup(postDTO, Long.parseLong(communityId));
            createdPost = postFacade.postToCommunityPostDTO(post);
        }

        return new ResponseEntity<>(createdPost, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> postDTOList = postService.getAllPosts()
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getAllPostsFromFollowedCommunities(Principal principal) {
        List<PostDTO> postDTOList = postService.getAllPostsFromFollowedCommunities(principal)
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);
    }

    @GetMapping("/user/posts")
    public ResponseEntity<List<PostDTO>> getAllPostsForUser(Principal principal) {
        List<PostDTO> postDTOList = postService.getAllPostsForUser(principal)
                .stream()
                .map(postFacade::postToPostDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);

    }

    @GetMapping("/community/{communityId}/posts")
    public ResponseEntity<List<CommunityPostDTO>> getAllPostsForCommunity(@PathVariable("communityId") String communityId) {
        List<CommunityPostDTO> postDTOList = postService.getAllPostsForCommunity(Long.parseLong(communityId))
                .stream()
                .map(postFacade::postToCommunityPostDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(postDTOList, HttpStatus.OK);

    }

    @PostMapping("/{postId}/{username}/like")
    public ResponseEntity<PostDTO> likePost(@PathVariable("postId") String postId,
                                            @PathVariable("username") String username) {
        Post post = postService.likePost(Long.parseLong(postId), username);
        PostDTO postDTO = postFacade.postToPostDTO(post);

        return new ResponseEntity<>(postDTO, HttpStatus.OK);

    }

    @PostMapping("/{postId}/delete")
    public ResponseEntity<MessageResponse> deletePost(@PathVariable("postId") String postId,
                                                      Principal principal) {
        postService.deletePost(Long.parseLong(postId), principal);

        return new ResponseEntity<>(new MessageResponse("Post was deleted"), HttpStatus.OK);
    }
}
