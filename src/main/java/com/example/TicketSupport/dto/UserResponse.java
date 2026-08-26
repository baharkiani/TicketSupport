package com.example.TicketSupport.dto;


import java.util.List;


public class UserResponse {
    private String username;
    private List<String> role;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }
}
