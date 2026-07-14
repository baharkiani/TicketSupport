package com.example.TicketSupport.controller;

import com.example.TicketSupport.dto.CreateTicketRequest;
import com.example.TicketSupport.dto.TicketResponse;
import com.example.TicketSupport.dto.UpdateTicketStatusRequest;
import com.example.TicketSupport.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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

    @GetMapping
    public ResponseEntity<Page<TicketResponse>> getAll(Pageable pageable){
        return ResponseEntity.ok(ticketService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getOne(@PathVariable Long id){
        return ResponseEntity.ok(ticketService.getOne(id));
    }

    @PatchMapping("/{id}/update")
    public ResponseEntity<TicketResponse> update(@PathVariable Long id, UpdateTicketStatusRequest reaquest){
        return ResponseEntity.ok(ticketService.update(id, reaquest));
    }




}
