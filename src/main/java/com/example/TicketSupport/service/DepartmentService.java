package com.example.TicketSupport.service;

import com.example.TicketSupport.dto.DepartmentRequest;
import com.example.TicketSupport.entity.Department;
import com.example.TicketSupport.repository.DepartmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(
            DepartmentRepository departmentRepository
    ) {
        this.departmentRepository = departmentRepository;
    }

    public Department create(DepartmentRequest request) {

        if (departmentRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException(
                    "Department already exists"
            );
        }

        Department department = new Department();
        department.setName(request.getName());

        return departmentRepository.save(department);
    }

    @Transactional(readOnly = true)
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Department getOne(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Department not found"
                        ));
    }

    public Department update(
            Long id,
            DepartmentRequest request
    ) {

        Department department = getOne(id);

        department.setName(request.getName());

        return departmentRepository.save(department);
    }

    public void delete(Long id) {

        Department department = getOne(id);

        departmentRepository.delete(department);
    }
}