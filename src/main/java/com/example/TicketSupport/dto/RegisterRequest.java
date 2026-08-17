package com.example.TicketSupport.dto;

import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterRequest {

    @NotBlank(message = "username should be filled")
    private String username;

    @NotBlank(message = "password should be filled")
    private String password;

    @NotNull
    private Role role;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public User mapToEntity(User user) {
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
