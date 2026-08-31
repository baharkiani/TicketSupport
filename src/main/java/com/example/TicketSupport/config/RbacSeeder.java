package com.example.TicketSupport.config;

import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.Role;
import com.example.TicketSupport.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(value = 1)
public class RbacSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;



    @Override
    public void run(String... args) {


        Role user = createRole("USER");
        Role admin = createRole("ADMIN");
        Role agent = createRole("AGENT");


        roleRepository.save(user);
        roleRepository.save(admin);
        roleRepository.save(agent);
    }


    private Role createRole(String name) {

        return roleRepository
                .findByRoleName(name)
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(name);
                    return roleRepository.save(role);
                });
    }
}