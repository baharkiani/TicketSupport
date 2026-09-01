package com.example.TicketSupport.policy;


import com.example.TicketSupport.entity.Department;
import com.example.TicketSupport.entity.Ticket;
import com.example.TicketSupport.repository.DepartmentRepository;
import com.example.TicketSupport.repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Map;

@Component
public class RequestDepartmentResolver {

    private final TicketRepository ticketRepository;
    private final DepartmentRepository departmentRepository;
    private final ObjectMapper objectMapper;

    public RequestDepartmentResolver(
            TicketRepository ticketRepository,
            DepartmentRepository departmentRepository,
            ObjectMapper objectMapper
    ) {
        this.ticketRepository = ticketRepository;
        this.departmentRepository = departmentRepository;
        this.objectMapper = objectMapper;
    }

    public Department resolve(PolicyContext context) {

        HttpServletRequest request = context.request();

        // اگر URL دارای id باشد
        Department department = resolveFromId(request);

        if (department != null) {
            return department;
        }

        // در غیر این صورت از Body
        return resolveFromBody(request);
    }

    private Department resolveFromId(
            HttpServletRequest request
    ) {

        @SuppressWarnings("unchecked")
        Map<String, String> variables =
                (Map<String, String>) request.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE
                );

        if (variables == null) {
            return null;
        }

        String id = variables.get("id");

        if (id == null) {
            return null;
        }

        try {

            Ticket ticket = ticketRepository.findById(Long.parseLong(id)).orElse(null);

            return ticket != null
                    ? ticket.getDepartment()
                    : null;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Department resolveFromBody(
            HttpServletRequest request
    ) {

        try {

            JsonNode body = objectMapper.readTree(request.getReader());

            JsonNode department = body.get("department");

            if (department == null ||
                    department.isNull()) {
                return null;
            }

            return departmentRepository
                    .findByName(department.asText())
                    .orElse(null);

        } catch (IOException e) {
            return null;
        }
    }
}