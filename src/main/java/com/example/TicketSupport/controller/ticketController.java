package com.example.TicketSupport.controller;

import com.example.TicketSupport.annotation.JwtRequired;
import com.example.TicketSupport.dto.CreateTicketRequest;
import com.example.TicketSupport.dto.TicketResponse;
import com.example.TicketSupport.dto.UpdateTicketStatusRequest;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.exception.UserOrPasswordNotFound;
import com.example.TicketSupport.repository.UserRepository;
import com.example.TicketSupport.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/tickets")
public class ticketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;

    public ticketController(TicketService ticketService,
                            UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }


    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest createTicketRequest
    , Authentication authentication) {
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.create(createTicketRequest, user.getId()));
    }

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAll(Pageable pageable, Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserOrPasswordNotFound("useranme not found")
        );
        Long departmentId = user.getDepartment().getId();

        return ResponseEntity.ok(ticketService.getAll(pageable, departmentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getOne(id));
    }


    @PatchMapping("/{id}/update")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id, @RequestBody UpdateTicketStatusRequest reaquest) {
        return ResponseEntity.ok(ticketService.update(id, reaquest));
    }


}
