package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.dto.TicketDTO;
import com.nikza.socialnetwork.entity.Ticket;
import com.nikza.socialnetwork.entity.TicketStatus;
import com.nikza.socialnetwork.entity.User;
import com.nikza.socialnetwork.exceptions.TicketNotFoundException;
import com.nikza.socialnetwork.repository.CommunityRepository;
import com.nikza.socialnetwork.repository.ImageRepository;
import com.nikza.socialnetwork.repository.PostRepository;
import com.nikza.socialnetwork.repository.TicketRepository;
import com.nikza.socialnetwork.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TicketService {

    public static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    private final PostRepository postRepository;
    private final TicketRepository ticketRepository;
    private final UserService userService;
    private final ImageRepository imageRepository;
    private final CommunityRepository communityRepository;

    @Autowired
    public TicketService(PostRepository postRepository, TicketRepository ticketRepository, UserService userService, ImageRepository imageRepository, CommunityRepository communityRepository) {
        this.postRepository = postRepository;
        this.ticketRepository = ticketRepository;
        this.userService = userService;
        this.imageRepository = imageRepository;
        this.communityRepository = communityRepository;
    }

    public Ticket createTicket(TicketDTO dto, Principal principal) {
        User creator = userService.getCurrentUser(principal);
        Ticket ticket = Ticket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .creator(creator)
                .build();

        LOG.info("saving Ticket by User: {}", creator.getEmail());
        return ticketRepository.save(ticket);
    }

    public Ticket addCandidate(Long ticketId, Principal principal) {
        User user = userService.getCurrentUser(principal);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        List<User> candidates = ticket.getCandidates();

        if (candidates.contains(user)) {
            candidates.remove(user);
            LOG.info("removing Candidate {} for Ticket: {}", user.getEmail(), ticketId);
        } else {
            candidates.add(user);
            LOG.info("adding Candidate {} for Ticket: {}", user.getEmail(), ticketId);
        }

        ticket.setCandidates(candidates);
        return ticketRepository.save(ticket);
    }

    public Ticket addSolver(Long ticketId, Long solverId, Principal principal) {
        User creator = userService.getCurrentUser(principal);
        User solver = userService.getUserById(solverId);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        List<User> candidates = ticket.getCandidates();

        if (creator.equals(ticket.getCreator()) && candidates.contains(solver)) {
            ticket.setCandidates(Collections.emptyList());
            ticket.setSolver(solver);
            LOG.info("added Solver {} for Ticket: {}", solverId, ticketId);

        }

        return ticketRepository.save(ticket);
    }

    public Ticket changeStatus(Long ticketId, TicketStatus status, Principal principal) {
        User solver = userService.getCurrentUser(principal);
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));

        if (solver.equals(ticket.getSolver())) {
            ticket.setStatus(status);
        }

        return ticketRepository.save(ticket);
    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedDateDesc();
    }
}