package com.example.TicketSupport.repository;

import com.example.TicketSupport.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findByRoleName(String roleName);
    boolean existsByRoleName(String roleName);
    boolean existsByPermissions_Id(Long permissionId);
}
