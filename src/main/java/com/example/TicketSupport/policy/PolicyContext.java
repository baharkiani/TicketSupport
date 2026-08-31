package com.example.TicketSupport.policy;


import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.security.PermissionDefinition;
import jakarta.servlet.http.HttpServletRequest;

public record PolicyContext(
        User user,
        PermissionDefinition permission,
        HttpServletRequest request
) {
}