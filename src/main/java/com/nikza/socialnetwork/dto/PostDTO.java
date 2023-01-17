package com.nikza.socialnetwork.dto;

import com.nikza.socialnetwork.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class PostDTO {

    private Long id;
    private String title;
    private String caption;
    private String location;
    private String username;
    private Integer likes;
    private Set<String> usersLiked;

    private List<Comment> comments;

    private LocalDateTime createdDate;

}
