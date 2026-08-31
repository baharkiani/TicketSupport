package com.example.TicketSupport.filter;

import com.example.TicketSupport.annotation.JwtRequired;
import com.example.TicketSupport.repository.RevokedTokenRepository;
import com.example.TicketSupport.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final RequestMappingHandlerMapping handlerMapping;
    private final UserDetailsService userDetailsService;
    private final RevokedTokenRepository revokedTokenRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            RequestMappingHandlerMapping handlerMapping,
            UserDetailsService userDetailsService,
            RevokedTokenRepository revokedTokenRepository
    ) {
        this.jwtService = jwtService;
        this.handlerMapping = handlerMapping;
        this.userDetailsService = userDetailsService;
        this.revokedTokenRepository = revokedTokenRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        //implement jwtFilter for specific method
//        HandlerMethod handlerMethod;
//
//        try {
//            Object handler = handlerMapping
//                    .getHandler(request)
//                    .getHandler();
//
//            if (!(handler instanceof HandlerMethod)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//
//            handlerMethod = (HandlerMethod) handler;
//
//        } catch (Exception e) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        boolean jwtRequired =
//                handlerMethod.hasMethodAnnotation(JwtRequired.class);
//
//        if (!jwtRequired) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        //jwtFilter Config
        String authHeader = request.getHeader("Authorization");

        try {

            String jwt = jwtService.extractToken(authHeader);

            request.setAttribute("jwtClaims", jwtService.extractClaims(jwt));

            if (jwt == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Authorization header is missing or invalid"
                );
                return;
            }

            if (!jwtService.validateAccessToken(jwt)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Invalid access token"
                );
                return;
            }

            String jti = jwtService.extractClaims(jwt).getId();

            if (revokedTokenRepository.existsByJti(jti)) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED,
                        "Access token has been revoked"
                );
                return;
            }

            String username = jwtService.extractUsername(jwt);

            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {

            SecurityContextHolder.clearContext();

            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Invalid access token"
            );
        }
    }
}