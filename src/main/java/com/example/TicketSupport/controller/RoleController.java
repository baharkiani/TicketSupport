package com.example.TicketSupport.controller;

import com.example.TicketSupport.dto.CreateRoleRequest;
import com.example.TicketSupport.dto.RoleResponse;
import com.example.TicketSupport.dto.UpdateRoleRequest;
import com.example.TicketSupport.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    private final RoleService roleService;
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }
    @PostMapping
    public ResponseEntity<RoleResponse> create(CreateRoleRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(roleService.create(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id, @RequestBody UpdateRoleRequest request) {



        return ResponseEntity.ok().body(roleService.updateRole(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
