package com.nikza.socialnetwork.facade;

import com.nikza.socialnetwork.dto.CommunityPostDTO;
import com.nikza.socialnetwork.dto.PostDTO;
import com.nikza.socialnetwork.entity.Post;
import org.springframework.stereotype.Component;

@Component
public class PostFacade {

    public PostDTO postToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setUsername(post.getUser().getUsername());
        postDTO.setId(post.getId());
        postDTO.setCaption(post.getCaption());
        postDTO.setLikes(post.getLikes());
        postDTO.setUsersLiked(post.getLikedUsers());
        postDTO.setLocation(post.getLocation());
        postDTO.setTitle(post.getTitle());
        return postDTO;
    }

    public CommunityPostDTO postToCommunityPostDTO(Post post) {
        CommunityPostDTO postDTO = new CommunityPostDTO();
        postDTO.setCommunityName(post.getCommunity().getName());
        postDTO.setId(post.getId());
        postDTO.setCaption(post.getCaption());
        postDTO.setLikes(post.getLikes());
        postDTO.setUsersLiked(post.getLikedUsers());
        postDTO.setLocation(post.getLocation());
        postDTO.setTitle(post.getTitle());
        return postDTO;
    }

}
