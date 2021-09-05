package com.nikza.socialnetwork.dto;

import lombok.Data;

import java.util.Set;

@Data
public class CommunityPostDTO {

    private Long id;
    private String title;
    private String caption;
    private String location;
    private String communityName;
    private Integer likes;
    private Set<String> usersLiked;

}
