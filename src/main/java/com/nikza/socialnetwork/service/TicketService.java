package com.nikza.socialnetwork.service;

import com.nikza.socialnetwork.entity.Ticket;
import com.nikza.socialnetwork.entity.User;
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
import java.util.List;

@Service
public class TicketService {

    public static final Logger LOG = LoggerFactory.getLogger(TicketService.class);

    private final PostRepository postRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final CommunityRepository communityRepository;

    @Autowired
    public TicketService(PostRepository postRepository, TicketRepository ticketRepository, UserRepository userRepository, ImageRepository imageRepository, CommunityRepository communityRepository) {
        this.postRepository = postRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
        this.communityRepository = communityRepository;
    }

//    public Ticket createTicket(TicketDTO dto, Principal principal) {
//        User user = getUserByPrincipal(principal);
//        Ticket ticket = Ticket.builder()
//                .title(dto.getTitle())
//                .description(dto.getDescription())
//                .likes(0)
//                .creator(user)
//                .solver(dto.getSolver())
//                .build();
//
//        LOG.info("saving Ticket for User: {}", user.getEmail());
//        return ticketRepository.save(ticket);
//    }

//    public Ticket followTicket(Long ticketId, Principal principal) {
//        User user = getUserByPrincipal(principal);
//        Ticket ticket = ticketRepository.findById(ticketId)
//                        .orElseThrow(() -> new TicketNotFoundException("ticket not found"));
//
//        ticket.setCandidates(ticket.getCandidates().add(user));
//        LOG.info("saving Ticket for User: {}", user.getEmail());
//        return ticketRepository.save(ticket);
//    }

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedDateDesc();
    }


    public User getUserByPrincipal(Principal principal) {
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("username not found"));
    }
}