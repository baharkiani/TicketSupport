package com.example.TicketSupport.policy;

import com.example.TicketSupport.entity.User;
import org.springframework.stereotype.Component;

@Component
public class DepartmentPolicy implements AuthorizationPolicy {

    private static final String API_PREFIX = "/api/";

    @Override
    public boolean supports(PolicyContext context) {

        String path = context.permission().getPath();

        return path.startsWith(API_PREFIX)
                && extractDepartment(path) != null;
    }

    @Override
    public boolean evaluate(PolicyContext context) {

        User user = context.user();

        if (user == null) {
            return false;
        }

        if (user.getDepartment() == null) {
            return false;
        }

        String departmentFromUrl =
                extractDepartment(
                        context.request().getRequestURI()
                );

        if (departmentFromUrl == null) {
            return false;
        }

        String userDepartment =
                user.getDepartment().getName();

        return userDepartment.equalsIgnoreCase(
                departmentFromUrl
        );
    }

    private String extractDepartment(String path) {

        String[] parts = path.split("/");

        /*
         * /api/IT/Tickets
         *
         * parts[0] = ""
         * parts[1] = api
         * parts[2] = IT
         * parts[3] = Tickets
         */

        if (parts.length < 4) {
            return null;
        }

        return parts[2];
    }
}
