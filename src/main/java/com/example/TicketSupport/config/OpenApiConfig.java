package com.example.TicketSupport.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .name("Authorization")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

//    // این کار رو انجام می‌ده که springdoc بهت بگه "Requires ROLE_ADMIN"
//    @Bean
//    public OperationCustomizer operationCustomizer() {
//        return (operation, handlerMethod) -> {
//            PreAuthorize preAuthorize = handlerMethod.getMethodAnnotation(PreAuthorize.class);
//            if (preAuthorize != null) {
//                String role = preAuthorize.value()
//                        .replace("hasRole('", "")
//                        .replace("')", "")
//                        .replace("hasAnyRole('", "")
//                        .replace("')", "");
//                operation.setDescription("Requires " + role + "\n\n" +
//                        (operation.getDescription() != null ? operation.getDescription() : ""));
//            }
//            return operation;
//        };
//    }
}