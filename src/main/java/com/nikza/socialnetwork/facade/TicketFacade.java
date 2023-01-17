package com.nikza.socialnetwork.facade;

import com.nikza.socialnetwork.dto.TicketDTO;
import com.nikza.socialnetwork.entity.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketFacade {

    public TicketDTO ticketToTicketDTO(Ticket ticket) {
        TicketDTO ticketDTO = new TicketDTO();
        ticketDTO.setId(ticket.getId());
        ticketDTO.setCreator(ticket.getCreator());
        ticketDTO.setSolver(ticket.getSolver());
        ticketDTO.setDescription(ticket.getDescription());
        ticketDTO.setComments(ticket.getComments());
        ticketDTO.setCandidates(ticket.getCandidates());
        ticketDTO.setCreatedDate(ticket.getCreatedDate());
        ticketDTO.setTitle(ticket.getTitle());
        ticketDTO.setIsSolved(ticket.getIsSolved());
        return ticketDTO;
    }
}