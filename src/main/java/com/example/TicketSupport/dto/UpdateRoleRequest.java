package com.example.TicketSupport.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateRoleRequest {

    @Size(min = 2, max = 50)
    private String roleName;

    private List<String> rolePermissions;

    public List<String> getRolePermissions() {
        return rolePermissions;
    }

    public void setRolePermissions(List<String> rolePermissions) {
        this.rolePermissions = rolePermissions;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}