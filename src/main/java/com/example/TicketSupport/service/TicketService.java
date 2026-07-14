package com.example.TicketSupport.service;

import com.example.TicketSupport.dto.CreateTicketRequest;
import com.example.TicketSupport.dto.TicketResponse;
import com.example.TicketSupport.entity.Ticket;
import com.example.TicketSupport.enums.TicketStatus;
import com.example.TicketSupport.repository.TicketRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
    @Transactional
    public TicketResponse create(CreateTicketRequest request) {
        Ticket ticket = new Ticket();
        mapToEntity(request, ticket);
        return toResponse(ticketRepository.save(ticket));
    }

    private void mapToEntity(CreateTicketRequest request, Ticket ticket) {
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(TicketStatus.OPEN);
    }

    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setTitle(ticket.getTitle());
        ticketResponse.setDescription(ticket.getDescription());
        ticketResponse.setStatus(ticket.getStatus());
        return ticketResponse;
    }
}
