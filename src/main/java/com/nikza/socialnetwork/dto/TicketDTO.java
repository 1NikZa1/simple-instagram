package com.nikza.socialnetwork.dto;

import com.nikza.socialnetwork.entity.Comment;
import com.nikza.socialnetwork.entity.User;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketDTO {

    private Long id;
    private String title;
    private String description;
    private User creator;
    private User solver;
    private List<Comment> comments;
    private List<User> candidates;
    private Boolean isSolved;
    private LocalDateTime createdDate;
}
