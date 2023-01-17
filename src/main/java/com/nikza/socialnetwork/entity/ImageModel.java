package com.nikza.socialnetwork.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
public class ImageModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageBytes;

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    private Long communityId;

    @JsonIgnore
    private Long postId;

    @JsonIgnore
    private Boolean isBackground;

}
