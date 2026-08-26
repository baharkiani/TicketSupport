package com.example.TicketSupport.dto;

import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.entity.User;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;

public class RegisterRequest {

    @NotBlank(message = "username should be filled")
    private String username;

    @NotBlank(message = "password should be filled")
    private String password;

    private Set<Role> roles;

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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public User mapToEntity(User user) {
        user.setUsername(username);
        user.setPassword(password);
        return user;
    }
}
