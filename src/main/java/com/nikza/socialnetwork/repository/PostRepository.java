package com.nikza.socialnetwork.repository;

import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findAllByUserOrderByCreatedDate(User user);
    List<Post> findAllByOrderByCreatedDateDesc();
    Optional<Post> findPostByIdAndUser(Long id,User user);
}
