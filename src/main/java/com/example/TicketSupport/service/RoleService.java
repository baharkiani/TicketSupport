package com.example.TicketSupport.service;

import com.example.TicketSupport.dto.CreateRoleRequest;
import com.example.TicketSupport.dto.RoleResponse;
import com.example.TicketSupport.dto.UpdateRoleRequest;
import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.repository.PermissionRepository;
import com.example.TicketSupport.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    public RoleResponse create(CreateRoleRequest request) {
        List<Permission> permissions =
                permissionRepository.findAllByNameIn(request.getPermissions());

        Role role = new Role();
        role.setRoleName(request.getRoleName().toUpperCase());
        role.setPermissions(new HashSet<>(permissions));
        roleRepository.save(role);
        RoleResponse response = new RoleResponse();
        response.setRoleName(request.getRoleName());
        return response;
    }

    @Transactional
    public RoleResponse updateRole(Long roleId, UpdateRoleRequest request) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));

        List<Permission> permissions =
                permissionRepository.findAllByNameIn(request.getRolePermissions());

        if (permissions.size() != request.getRolePermissions().size()) {
            throw new IllegalArgumentException("Some permissions not found");
        }

        role.setRoleName(request.getRoleName().toUpperCase());
        role.setPermissions(new HashSet<>(permissions));

        roleRepository.save(role);

        RoleResponse response = new RoleResponse();
        response.setRoleName(request.getRoleName());
        return response;
    }

    @Transactional
    public void delete(Long id) {

        Role role = roleRepository.findById(id).orElseThrow(() ->
                        new IllegalArgumentException("Role not found"));

        role.getUsers().forEach(user ->
                user.getRoles().remove(role)
        );

        role.getPermissions().clear();

        roleRepository.delete(role);
    }
}
