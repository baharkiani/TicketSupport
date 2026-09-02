package com.example.TicketSupport.dto;

import jakarta.validation.constraints.NotBlank;

public class PermissionRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
