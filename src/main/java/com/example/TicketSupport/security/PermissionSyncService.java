package com.example.TicketSupport.security;

import com.example.TicketSupport.security.PermissionDefinition;


import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.repository.PermissionRepository;
import com.example.TicketSupport.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PermissionSyncService {

    private final PermissionScanner permissionScanner;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionAssignmentRule permissionAssignmentRule;

    public PermissionSyncService(
            PermissionScanner permissionScanner,
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            PermissionAssignmentRule permissionAssignmentRule
    ) {
        this.permissionScanner = permissionScanner;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.permissionAssignmentRule = permissionAssignmentRule;
    }

    public void synchronize() {

        List<PermissionDefinition> definitions = permissionScanner.scan();

        List<Role> roles = roleRepository.findAll();

        for (PermissionDefinition definition : definitions) {

            Permission permission = permissionRepository
                    .findByName(definition.name())
                    .orElseGet(Permission::new);

            permission.setName(definition.name());

            permissionRepository.save(permission);

            assignPermission(
                    permission,
                    definition,
                    roles
            );
        }

        roleRepository.saveAll(roles);
    }

    private void assignPermission(
            Permission permission,
            PermissionDefinition definition,
            List<Role> roles
    ) {

        for (Role role : roles) {

            if (permissionAssignmentRule.matches(
                    role,
                    definition
            )) {

                role.getPermissions()
                        .add(permission);
            }
        }
    }
}