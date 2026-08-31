package com.example.TicketSupport.dto;

import jakarta.validation.constraints.NotBlank;

public class UnlockUserRequest {
    @NotBlank
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
