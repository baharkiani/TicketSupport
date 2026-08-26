package com.example.TicketSupport.filter;

import com.example.TicketSupport.annotation.JwtRequired;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.repository.UserRepository;
import com.example.TicketSupport.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RequestMappingHandlerMapping handlerMapping;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            RequestMappingHandlerMapping handlerMapping,
            UserRepository userRepository,
            UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.handlerMapping = handlerMapping;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        //implement jwtFilter for specific method
        HandlerMethod handlerMethod;

        try {
            Object handler = handlerMapping
                    .getHandler(request)
                    .getHandler();

            if (!(handler instanceof HandlerMethod)) {
                filterChain.doFilter(request, response);
                return;
            }

            handlerMethod = (HandlerMethod) handler;

        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        }

        boolean jwtRequired =
                handlerMethod.hasMethodAnnotation(JwtRequired.class);

        if (!jwtRequired) {
            filterChain.doFilter(request, response);
            return;
        }

        //jwtFilter Config
        String authHeader = request.getHeader("Authorization");


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwt = authHeader.substring(7).trim();

        try {


            if (!jwtService.validateAccessToken(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }


            String username = jwtService.extractUsername(jwt);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);


            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, //principal (user info)
                            null,
                            userDetails.getAuthorities() //authorities (role,...)
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}