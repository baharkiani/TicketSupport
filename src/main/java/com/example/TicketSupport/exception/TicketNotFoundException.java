package com.example.TicketSupport.exception;

public class TicketNotFoundException extends RuntimeException{
    public TicketNotFoundException(Long id){
        super("Ticket Not Found with id: " + id);
    }
}
