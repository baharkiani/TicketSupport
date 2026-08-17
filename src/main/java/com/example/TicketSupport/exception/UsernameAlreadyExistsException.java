package com.example.TicketSupport.exception;

import com.example.TicketSupport.entity.User;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String username) {
        super(username + "already exists");
    }
}
