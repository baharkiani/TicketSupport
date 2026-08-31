package com.example.TicketSupport.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PermissionInUseException extends RuntimeException {
    public PermissionInUseException(String message) {
        super(message);
    }
}
