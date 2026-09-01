package com.example.TicketSupport.security;

import com.example.TicketSupport.entity.Permission;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.policy.AuthorizationPolicy;
import com.example.TicketSupport.policy.PolicyContext;
import com.example.TicketSupport.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class CustomAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    private final PermissionScanner permissionScanner;
    private final UserRepository userRepository;
    private final List<AuthorizationPolicy> policies;

    public CustomAuthorizationManager(
            PermissionScanner permissionScanner,
            UserRepository userRepository,
            List<AuthorizationPolicy> policies
    ) {
        this.permissionScanner = permissionScanner;
        this.userRepository = userRepository;
        this.policies = policies;
    }

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context
    ) {

        // 1. گرفتن Request واقعی
        HttpServletRequest request = context.getRequest();


        // 2. گرفتن Authentication
        Authentication auth = authentication.get();

        // 3. کاربر لاگین نکرده
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        // 4. پیدا کردن Permission مربوط به Endpoint
        PermissionDefinition permission = findPermission(request);


        System.out.println("Permission: " +
                (permission != null ? permission.name() : "NULL"));


        if (permission == null) {
            return new AuthorizationDecision(false);
        }

        // 5. گرفتن User ID از JWT subject
//        String username = auth.getName();


        // 6. پیدا کردن User با ID
//        System.out.println("BEFORE FIND USER");
//
//        String username = auth.getName();
//
//        System.out.println("USERNAME = " + username);
//
//        User user = userRepository
//                .findByUsername(username)
//                .orElse(null);
//
//        System.out.println("AFTER FIND USER");
        System.out.println("BEFORE FIND USER");

        String username = auth.getName();

        System.out.println("USERNAME = [" + username + "]");

        User user;

        try {

            System.out.println("CALLING REPOSITORY...");

            user = userRepository
                    .findByUsername(username)
                    .orElse(null);

            System.out.println("REPOSITORY RETURNED");

        } catch (Exception e) {

            System.out.println("========== REPOSITORY ERROR ==========");
            e.printStackTrace();

            return new AuthorizationDecision(false);
        }

        System.out.println("AFTER FIND USER");

        if (user == null) {
            System.out.println("USER NOT FOUND");
            return new AuthorizationDecision(false);
        }

        System.out.println("USER FOUND = " + user.getUsername());

        if (user.getDepartment() == null) {
            System.out.println("USER DEPARTMENT = NULL");
        } else {
            System.out.println(
                    "USER DEPARTMENT ID = " +
                            user.getDepartment().getId()
            );
        }

        System.out.println("BEFORE HAS PERMISSION");

        boolean hasPermission =
                hasPermission(user, permission.name());

        System.out.println(
                "AFTER HAS PERMISSION = " + hasPermission
        );

        if (!hasPermission) {
            return new AuthorizationDecision(false);
        }

        // 7. بررسی Permission






        // 8. ساخت PolicyContext
        PolicyContext policyContext =
                new PolicyContext(
                        user,
                        permission,
                        request
                );

        // 9. اجرای Policyها

//        for (AuthorizationPolicy policy : policies) {
//
//
//            // این Policy مربوط به این Request نیست
//            if (!policy.supports(policyContext)) {
//                continue;
//            }
//
//            // Policy اجازه دسترسی نداد
//            if (!policy.evaluate(policyContext)) {
//                return new AuthorizationDecision(false);
//            }
//        }
        System.out.println("POLICIES COUNT = " + policies.size());

        for (AuthorizationPolicy policy : policies) {

            System.out.println(
                    "POLICY = " + policy.getClass().getName()
            );

            boolean supports = policy.supports(policyContext);

            System.out.println(
                    "SUPPORTS = " + supports
            );

            if (!supports) {
                continue;
            }

            System.out.println("BEFORE EVALUATE");

            if (!policy.evaluate(policyContext)) {
                return new AuthorizationDecision(false);
            }

            System.out.println("AFTER EVALUATE");
        }


        // 10. دسترسی مجاز است
        System.out.println("=== BEFORE RETURN TRUE ===");

        return new AuthorizationDecision(true);
    }

    /**
     * پیدا کردن Permission مربوط به Request
     */
//    private PermissionDefinition findPermission(
//            HttpServletRequest request
//    ) {
//
//        String requestMethod = request.getMethod();
//        String requestPath = request.getRequestURI();
//
//        return permissionScanner.scan()
//                .stream()
//                .filter(permission ->
//                        permission.method()
//                                .name()
//                                .equalsIgnoreCase(requestMethod)
//                )
//                .filter(permission ->
//                        matchesPath(
//                                permission.path(),
//                                requestPath
//                        )
//                )
//                .findFirst()
//                .orElse(null);
//    }
    private PermissionDefinition findPermission(
            HttpServletRequest request
    ) {

        String requestMethod = request.getMethod();
        String requestPath = request.getRequestURI();
        System.out.println("ACTUAL METHOD = [" + request.getMethod() + "]");
        System.out.println("ACTUAL PATH = [" + request.getRequestURI() + "]");

        System.out.println("=== FIND PERMISSION ===");
        System.out.println("Request Method: " + requestMethod);
        System.out.println("Request Path: " + requestPath);

        List<PermissionDefinition> definitions =
                permissionScanner.scan();

        for (PermissionDefinition permission : definitions) {

            System.out.println(
                    "Checking: method=" + permission.method()
                            + " path=" + permission.path()
                            + " name=" + permission.name()
            );

            boolean methodMatch =
                    permission.method()
                            .name()
                            .equalsIgnoreCase(requestMethod);

            boolean pathMatch =
                    matchesPath(
                            permission.path(),
                            requestPath
                    );

            System.out.println(
                    "Method Match: " + methodMatch
                            + " | Path Match: " + pathMatch
            );

            if (methodMatch && pathMatch) {

                System.out.println(
                        "MATCHED PERMISSION: "
                                + permission.name()
                );

                return permission;
            }
        }

        System.out.println("NO PERMISSION FOUND");

        return null;
    }

    /**
     * مقایسه Path تعریف شده Permission
     * با Path واقعی Request
     * <p>
     * مثال:
     * <p>
     * /api/tickets/{id}
     * <p>
     * با:
     * <p>
     * /api/tickets/15
     * <p>
     * برابر محسوب می‌شود.
     */
    private boolean matchesPath(
            String permissionPath,
            String requestPath
    ) {

        System.out.println("=== MATCH PATH ===");
        System.out.println("Permission Path = [" + permissionPath + "]");
        System.out.println("Request Path    = [" + requestPath + "]");

        String[] permissionParts =
                permissionPath.split("/");

        String[] requestParts =
                requestPath.split("/");

        System.out.println(
                "Permission Parts = " +
                        java.util.Arrays.toString(permissionParts)
        );

        System.out.println(
                "Request Parts = " +
                        java.util.Arrays.toString(requestParts)
        );

        if (permissionParts.length != requestParts.length) {
            System.out.println("LENGTH NOT MATCH");
            return false;
        }

        for (int i = 0;
             i < permissionParts.length;
             i++) {

            String permissionPart =
                    permissionParts[i];

            String requestPart =
                    requestParts[i];

            System.out.println(
                    "Comparing [" +
                            permissionPart +
                            "] with [" +
                            requestPart +
                            "]"
            );

            if (permissionPart.startsWith("{")
                    && permissionPart.endsWith("}")) {

                continue;
            }

            if (!permissionPart.equals(requestPart)) {
                System.out.println("PART NOT MATCH");
                return false;
            }
        }

        System.out.println("PATH MATCH = TRUE");

        return true;
    }


    private boolean hasPermission(
            User user,
            String permissionName
    ) {
        System.out.println("ENTER HAS PERMISSION");

        boolean result = user.getRoles()
                .stream()
                .flatMap(role ->
                        role.getPermissions().stream()
                )
                .map(Permission::getName)
                .anyMatch(permissionName::equals);

        System.out.println("HAS PERMISSION RESULT = " + result);

        return result;
    }
}