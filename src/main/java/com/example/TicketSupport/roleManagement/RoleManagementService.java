package com.example.TicketSupport.roleManagement;

import com.example.TicketSupport.dto.*;
import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.exception.PermissionInUseException;
import com.example.TicketSupport.repository.PermissionRepository;
import com.example.TicketSupport.repository.RoleRepository;
import com.example.TicketSupport.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleManagementService implements RoleManagement {
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private PermissionRepository permissionRepository;

    //RoleService
    @Override
    public RoleResponse createRole(CreateRoleRequest request) {

        if (roleRepository.existsByRoleName(request.getRoleName())) {
            throw new IllegalArgumentException(
                    "Role already exists: " + request.getRoleName()
            );
        }

        Role role = new Role();
        role.setRoleName(request.getRoleName());
        return toResponse(roleRepository.save(role));
    }


    @Override
    public Role updateRole(Long roleId, UpdateRoleRequest request) {
        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new IllegalArgumentException("Role Not Found"));
        role.setRoleName(request.getRoleName());
        return roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId).orElseThrow(() ->
                new IllegalArgumentException("Role Not Found"));
        roleRepository.deleteById(role.getId());
    }

    @Override
    public Role getRole(Long roleId) {
        return roleRepository.findById(roleId).orElseThrow(() ->
                new IllegalArgumentException("Role Not Found"));
    }




    private RoleResponse toResponse(Role role) {

        RoleResponse roleResponse = new RoleResponse();

        roleResponse.setId(role.getId());
        roleResponse.setRoleName(role.getRoleName());

        roleResponse.setPermissions(
                role.getPermissions()
                        .stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet())
        );

        return roleResponse;
    }

}
