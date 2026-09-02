package com.example.TicketSupport.security;

import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.policy.AuthorizationPolicy;
import com.example.TicketSupport.policy.PolicyContext;
import com.example.TicketSupport.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class CustomAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final PermissionScanner permissionScanner;
    private final UserRepository userRepository;
    private final List<AuthorizationPolicy> policies;


    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context
    ) {

        HttpServletRequest request = context.getRequest();

        Authentication auth = authentication.get();
        //authentication check
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        //find all permissions that match to request
        PermissionDefinition permission = findPermission(request);

        if (permission == null) {
            return new AuthorizationDecision(false);
        }

        //find user
        String username = auth.getName();
        User user;

        try {
            user = userRepository.findByUsername(username).orElse(null);

        } catch (Exception e) {
            e.printStackTrace();
            return new AuthorizationDecision(false);
        }

        if (user == null) {
            return new AuthorizationDecision(false);
        }

        //check user has permission or not
        boolean hasPermission = hasPermission(user, permission.name());


        if (!hasPermission) {
            return new AuthorizationDecision(false);
        }



        //  PolicyContext for DepartmentPolicy
        PolicyContext policyContext = new PolicyContext(user, permission, request);


        for (AuthorizationPolicy policy : policies) {
            //check is this policy for this request
            boolean supports = policy.supports(policyContext);

            if (!supports) {
                continue;
            }


            if (!policy.evaluate(policyContext)) {
                return new AuthorizationDecision(false);
            }
        }


        return new AuthorizationDecision(true);
    }




    private PermissionDefinition findPermission(HttpServletRequest request) {

        String requestMethod = request.getMethod();
        String requestPath = request.getRequestURI();

        List<PermissionDefinition> definitions = permissionScanner.scan();

        for (PermissionDefinition permission : definitions) {

            boolean methodMatch = permission.method().name().equalsIgnoreCase(requestMethod);
            boolean pathMatch = matchesPath(permission.path(), requestPath);

            if (methodMatch && pathMatch) {
                return permission;
            }
        }

        return null;
    }


    private boolean matchesPath(String permissionPath, String requestPath) {

        String[] permissionParts = permissionPath.split("/");
        String[] requestParts = requestPath.split("/");


        if (permissionParts.length != requestParts.length) {
            return false;
        }

        for (int i = 0; i < permissionParts.length; i++) {

            String permissionPart = permissionParts[i];
            String requestPart = requestParts[i];

            if (permissionPart.startsWith("{") && permissionPart.endsWith("}")) {
                continue;
            }

            if (!permissionPart.equals(requestPart)) {
                return false;
            }
        }

        return true;
    }


    private boolean hasPermission(User user, String permissionName) {

        boolean isAdmin = user.getRoles()
                .stream()
                .anyMatch(role ->
                        role.getRoleName().equalsIgnoreCase("ADMIN")
                );

        if (isAdmin) {
            return true;
        }

        boolean result = user.getRoles().stream().flatMap(role ->
                        role.getPermissions().stream())
                .map(Permission::getName)
                .anyMatch(permissionName::equals);

        return result;
    }
}