package com.example.TicketSupport.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(value = 2)
public class PermissionSeeder implements CommandLineRunner {

    private final PermissionSyncService permissionSyncService;

    @Override
    public void run(String... args) {

        permissionSyncService.synchronize();

    }
}