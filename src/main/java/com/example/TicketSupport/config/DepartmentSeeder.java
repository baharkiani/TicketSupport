package com.example.TicketSupport.config;

import com.example.TicketSupport.entity.Department;
import com.example.TicketSupport.repository.DepartmentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DepartmentSeeder implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;

    public DepartmentSeeder(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void run(String... args) {

        List<String> departments = List.of(
                "IT",
                "FINANCE",
                "HR",
                "SALES"
        );

        for (String name : departments) {

            if (departmentRepository.findByName(name).isEmpty()) {

                Department department = new Department();
                department.setName(name);

                departmentRepository.save(department);
            }
        }
    }
}