package com.example.TicketSupport.security;

import org.springframework.web.bind.annotation.RequestMethod;

public class PermissionDefinition {

    private String name;
    private String path;
    private RequestMethod method;
    private String controller;
    private String handler;

    public PermissionDefinition(
            String name,
            String path,
            RequestMethod method,
            String controller,
            String handler
    ) {
        this.name = name;
        this.path = path;
        this.method = method;
        this.controller = controller;
        this.handler = handler;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getController() {
        return controller;
    }

    public String getHandler() {
        return handler;
    }
}