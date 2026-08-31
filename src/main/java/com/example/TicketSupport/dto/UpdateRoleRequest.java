package com.example.TicketSupport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateRoleRequest {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50)
    private String roleName;


    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}