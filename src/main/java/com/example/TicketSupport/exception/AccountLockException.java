package com.example.TicketSupport.exception;

public class AccountLockException extends RuntimeException {
    public AccountLockException(String message) {
        super("Account locked: " + message);
    }
}
