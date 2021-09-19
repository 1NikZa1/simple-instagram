package com.nikza.socialnetwork.repository;

import com.nikza.socialnetwork.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    Optional<Community> findById(Long id);

    List<Community> findAllByOrderByName();

    List<Community> findAllByUsers_id(Long id);
}
