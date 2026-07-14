package com.example.TicketSupport.controller;

import com.example.TicketSupport.dto.CreateTicketRequest;
import com.example.TicketSupport.dto.TicketResponse;
import com.example.TicketSupport.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tickets")
public class ticketController {

    private final TicketService ticketService;
    public ticketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(@Valid @RequestBody CreateTicketRequest createTicketRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.create(createTicketRequest));
    }



}
