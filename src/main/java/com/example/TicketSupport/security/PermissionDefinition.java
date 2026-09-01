package com.example.TicketSupport.security;

import org.springframework.web.bind.annotation.RequestMethod;

public record PermissionDefinition(
        String name,
        String path,
        RequestMethod method,
        String controller,
        String handler,
        String action
) {
}
