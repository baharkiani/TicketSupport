package com.example.TicketSupport.service;

import com.example.TicketSupport.annotation.LogExecutionTime;
import com.example.TicketSupport.dto.CreateTicketRequest;
import com.example.TicketSupport.dto.TicketResponse;
import com.example.TicketSupport.dto.UpdateTicketStatusRequest;
import com.example.TicketSupport.entity.Ticket;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.exception.TicketNotFoundException;
import com.example.TicketSupport.exception.UserOrPasswordNotFound;
import com.example.TicketSupport.repository.TicketRepository;
import com.example.TicketSupport.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
public class TicketService  {
    public final TicketRepository ticketRepository;
    public final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TicketResponse create(CreateTicketRequest request,long userId) {
        Ticket ticket = new Ticket();
        User user = userRepository.findById(userId).orElseThrow(()->
                new IllegalArgumentException("user not found"));
        // فقط کاربر دارای دپارتمان می‌تواند تیکت ایجاد کند
        if (user.getDepartment() == null) {
            throw new IllegalArgumentException(
                    "User must belong to a department to create a ticket"
            );
        }
        request.mapToEntity(ticket);

        ticket.setDepartment(user.getDepartment());
        return toResponse(ticketRepository.save(ticket));
    }

    @Transactional(readOnly = true)
    public Page<TicketResponse> getAll(Pageable pageable, Authentication authentication ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserOrPasswordNotFound("useranme not found")
        );
        Long departmentId = user.getDepartment().getId();

        Page<Ticket> tickets = ticketRepository.findByDepartmentId(departmentId, pageable);

        return tickets.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    @LogExecutionTime
    public TicketResponse getOne(Long id) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
        return toResponse(ticket);
    }

    @Transactional
    public TicketResponse update(Long id, UpdateTicketStatusRequest request) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new TicketNotFoundException(id));
        ticket.setStatus(request.getStatus());
        return toResponse(ticketRepository.save(ticket));

    }



    private TicketResponse toResponse(Ticket ticket) {
        TicketResponse ticketResponse = new TicketResponse();
        ticketResponse.setId(ticket.getId());
        ticketResponse.setTitle(ticket.getTitle());
        ticketResponse.setDescription(ticket.getDescription());
        ticketResponse.setStatus(ticket.getStatus());
        ticketResponse.setCreatedAt(ticket.getCreatedAt());
        ticketResponse.setUpdatedAt(ticket.getUpdatedAt());
        return ticketResponse;
    }


}
