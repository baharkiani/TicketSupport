package com.example.TicketSupport.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateRoleRequest {
    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50)
    private String roleName;

    public CreateRoleRequest(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
