package com.example.TicketSupport.service;

import com.example.TicketSupport.dto.PermissionRequest;
import com.example.TicketSupport.dto.PermissionResponse;
import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.repository.PermissionRepository;
import com.example.TicketSupport.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public PermissionResponse create(PermissionRequest request) {

        if (permissionRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("Permission already exists");
        }

        Permission permission = new Permission();
        permission.setName(request.getName().toUpperCase());

        return toResponse(permissionRepository.save(permission));
    }

    @Transactional(readOnly = true)
    public List<PermissionResponse> getAll() {

        return permissionRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PermissionResponse getById(Long id) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Permission not found"));

        return toResponse(permission);
    }

    public PermissionResponse update(
            Long id,
            PermissionRequest request
    ) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Permission not found"));

        permission.setName(request.getName().toUpperCase());

        return toResponse(permissionRepository.save(permission));
    }
    @Transactional
    public void delete(Long id) {

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Permission not found"));

        List<Role> roles = roleRepository.findAll();

        for (Role role : roles) {
            role.getPermissions().remove(permission);
        }

        permissionRepository.delete(permission);
    }

    private PermissionResponse toResponse(Permission permission) {

        PermissionResponse response = new PermissionResponse();

        response.setId(permission.getId());
        response.setName(permission.getName());

        return response;
    }
}
