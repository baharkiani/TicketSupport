package com.example.TicketSupport.config;

import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.repository.PermissionRepository;
import com.example.TicketSupport.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RbacSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {

        seedUserRole();
        seedAgentRole();
        seedAdminRole();
    }

    private void seedUserRole() {

        if (roleRepository.findByRoleName("USER").isPresent()) {
            return;
        }

        Role role = new Role();
        role.setRoleName("USER");

        Set<Permission> permissions = new HashSet<>();

        addPermission(permissions, "TICKET_READ");
        addPermission(permissions, "TICKET_CREATE");

        role.setPermissions(permissions);

        roleRepository.save(role);
    }

    private void seedAgentRole() {

        if (roleRepository.findByRoleName("AGENT").isPresent()) {
            return;
        }

        Role role = new Role();
        role.setRoleName("AGENT");

        Set<Permission> permissions = new HashSet<>();

        addPermission(permissions, "TICKET_READ");
        addPermission(permissions, "TICKET_CREATE");
        addPermission(permissions, "TICKET_UPDATE");

        role.setPermissions(permissions);

        roleRepository.save(role);
    }

    private void seedAdminRole() {

        if (roleRepository.findByRoleName("ADMIN").isPresent()) {
            return;
        }

        Role role = new Role();
        role.setRoleName("ADMIN");

        Set<Permission> permissions =
                new HashSet<>(permissionRepository.findAll());

        role.setPermissions(permissions);

        roleRepository.save(role);
    }

    private void addPermission(
            Set<Permission> permissions,
            String permissionName
    ) {

        permissionRepository
                .findByName(permissionName)
                .ifPresent(permissions::add);
    }
}