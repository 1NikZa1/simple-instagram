package com.nikza.socialnetwork.repository;

import com.nikza.socialnetwork.entity.Community;
import com.nikza.socialnetwork.entity.Post;
import com.nikza.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByUserOrderByCreatedDate(User user);

    List<Post> findAllByUser_idOrderByCreatedDate(Long userId);

    List<Post> findAllByCommunityOrderByCreatedDate(Community community);

    List<Post> findAllByOrderByCreatedDateDesc();

    Optional<Post> findPostByIdAndUser(Long id, User user);
}
