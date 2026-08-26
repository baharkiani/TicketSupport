package com.example.TicketSupport.config;

import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.repository.PermissionRepository;
import com.example.TicketSupport.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RbacSeeder implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;



    @Override
    public void run(String... args) {

        Permission read = createPermission("TICKET_READ");
        Permission create = createPermission("TICKET_CREATE");
        Permission update = createPermission("TICKET_UPDATE");
        Permission delete = createPermission("TICKET_DELETE");

        Role user = createRole("USER");
        Role admin = createRole("ADMIN");

        user.setPermissions(
                new HashSet<>(Set.of(
                        read,
                        create
                ))
        );

        admin.setPermissions(
                new HashSet<>(Set.of(
                        read,
                        create,
                        update,
                        delete
                ))
        );

        roleRepository.save(user);
        roleRepository.save(admin);
    }

    private Permission createPermission(String name) {

        return permissionRepository
                .findByName(name)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(name);
                    return permissionRepository.save(permission);
                });
    }

    private Role createRole(String name) {

        return roleRepository
                .findByRoleName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(name);
                    return roleRepository.save(role);
                });
    }
}