package com.nikza.socialnetwork.repository;

import com.nikza.socialnetwork.entity.Ticket;
import com.nikza.socialnetwork.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByCreator(User creator);

    List<Ticket> findAllBySolver(User creator);

    List<Ticket> findAllByOrderByCreatedDateDesc();

    List<Ticket> findAllByTitleIsLikeIgnoreCaseOrDescriptionLikeIgnoreCase(String title, String description);
}