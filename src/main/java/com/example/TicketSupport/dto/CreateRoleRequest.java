package com.example.TicketSupport.dto;

import com.example.TicketSupport.entity.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class CreateRoleRequest {
    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50)
    private String roleName;


    @NotBlank
    private List<String> permissions;

    public CreateRoleRequest(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }


    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
