package com.example.TicketSupport.dto;


import com.example.TicketSupport.enums.Role;

public class UserResponse {
    private String username;
    private Role role;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
