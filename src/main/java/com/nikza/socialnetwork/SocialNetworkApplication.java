package com.nikza.socialnetwork;

import com.nikza.socialnetwork.dto.PostDTO;
import com.nikza.socialnetwork.dto.UserDTO;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SocialNetworkApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocialNetworkApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Post.class, PostDTO.class).addMappings(
                mapper -> mapper.map(src -> src.getUser().getUsername(), PostDTO::setUsername)
        );

        modelMapper.typeMap(User.class, UserDTO.class).addMappings(
                mapper -> mapper.map(User::getName, UserDTO::setFirstname)
        );

        return modelMapper;
    }

}
