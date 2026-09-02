package com.example.TicketSupport.controller;

import com.example.TicketSupport.dto.PermissionRequest;
import com.example.TicketSupport.dto.PermissionResponse;
import com.example.TicketSupport.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    public ResponseEntity<PermissionResponse> create(
            @RequestBody PermissionRequest request
    ) {
        return ResponseEntity.ok(permissionService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<PermissionResponse>> getAll() {

        return ResponseEntity.ok(permissionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(permissionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponse> update(@PathVariable Long id, @RequestBody PermissionRequest request) {

        return ResponseEntity.ok(permissionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
