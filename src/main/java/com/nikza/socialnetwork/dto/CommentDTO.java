package com.nikza.socialnetwork.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {

    private Long id;
    @NotEmpty
    private String message;
    private String username;
    private LocalDateTime createdDate;
}
