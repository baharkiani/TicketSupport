package com.example.TicketSupport.policy;

public interface AuthorizationPolicy {

    boolean supports(PolicyContext context);

    boolean evaluate(PolicyContext context);
}