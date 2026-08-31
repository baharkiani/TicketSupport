package com.example.TicketSupport.security;

import com.example.TicketSupport.entity.Role;
import org.springframework.stereotype.Component;

@Component
public class PermissionAssignmentRule {
    public boolean matches(
            Role role,
            PermissionDefinition permission
    ) {

        String roleName =
                role.getRoleName().toUpperCase();

        String action =
                extractAction(permission.getName());

        return switch (roleName) {

            case "USER" -> action.equals("READ")
                    || action.equals("CREATE");

            case "AGENT" -> action.equals("READ")
                    || action.equals("CREATE")
                    || action.equals("UPDATE");

            case "ADMIN" -> true;

            default -> false;
        };
    }

    private String extractAction(String permissionName) {

        int index =
                permissionName.lastIndexOf("_");

        return permissionName
                .substring(index + 1)
                .toUpperCase();
    }
}
