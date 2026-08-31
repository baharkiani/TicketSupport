package com.example.TicketSupport.roleManagement;

import com.example.TicketSupport.dto.*;
import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import org.springframework.data.domain.Page;

import java.awt.print.Pageable;

public interface RoleManagement {

    // Role
    RoleResponse createRole(CreateRoleRequest request);

    Role updateRole(Long roleId, UpdateRoleRequest request);

    void deleteRole(Long roleId);

    Role getRole(Long roleId);



}
