package com.example.TicketSupport.exception;

public class UserOrPasswordNotFound extends RuntimeException {
    public UserOrPasswordNotFound(String message) {
        super(message);
    }
}
