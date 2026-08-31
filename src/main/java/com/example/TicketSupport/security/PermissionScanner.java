package com.example.TicketSupport.security;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class PermissionScanner {

    private final RequestMappingHandlerMapping handlerMapping;

    public PermissionScanner(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    public List<PermissionDefinition> scan() {

        return handlerMapping.getHandlerMethods()
                .entrySet()
                .stream()
                .flatMap(entry -> createDefinitions(
                        entry.getKey(),
                        entry.getValue()
                ).stream())
                .toList();
    }

    private List<PermissionDefinition> createDefinitions(
            RequestMappingInfo mapping,
            HandlerMethod handlerMethod
    ) {

        Set<RequestMethod> methods =
                mapping.getMethodsCondition().getMethods();

        if (methods.isEmpty()) {
            return List.of();
        }

        Set<String> paths =
                mapping.getPathPatternsCondition()
                        .getPatternValues();

        List<PermissionDefinition> definitions = new ArrayList<>();

        for (RequestMethod method : methods) {

            String permissionName =
                    generatePermissionName(handlerMethod, method);

            for (String path : paths) {

                definitions.add(
                        new PermissionDefinition(
                                permissionName,
                                path,
                                method,
                                handlerMethod.getBeanType().getSimpleName(),
                                handlerMethod.getMethod().getName()
                        )
                );
            }
        }

        return definitions;
    }

    private String generatePermissionName(
            HandlerMethod handlerMethod,
            RequestMethod method
    ) {

        String resource = handlerMethod
                .getBeanType()
                .getSimpleName()
                .replace("Controller", "")
                .toUpperCase();

        String action = switch (method) {
            case GET -> "READ";
            case POST -> "CREATE";
            case PUT, PATCH -> "UPDATE";
            case DELETE -> "DELETE";
            default -> method.name();
        };

        return resource + "_" + action;
    }
}
