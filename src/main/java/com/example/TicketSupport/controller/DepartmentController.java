package com.example.TicketSupport.controller;

import com.example.TicketSupport.dto.DepartmentRequest;
import com.example.TicketSupport.entity.Department;
import com.example.TicketSupport.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(
            DepartmentService departmentService
    ) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<Department> create(
            @Valid @RequestBody DepartmentRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(departmentService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAll() {
        return ResponseEntity.ok(
                departmentService.getAll()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getOne(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                departmentService.getOne(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Department> update(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request
    ) {
        return ResponseEntity.ok(departmentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
