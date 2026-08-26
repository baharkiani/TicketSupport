package com.example.TicketSupport.service;

import com.example.TicketSupport.dto.LoginRequest;
import com.example.TicketSupport.dto.LoginResponse;
import com.example.TicketSupport.dto.RegisterRequest;
import com.example.TicketSupport.dto.UserResponse;
import com.example.TicketSupport.entity.RefreshToken;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.exception.UserOrPasswordNotFound;
import com.example.TicketSupport.exception.UsernameAlreadyExistsException;
import com.example.TicketSupport.repository.RefreshTokenRepository;
import com.example.TicketSupport.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Transactional
    public UserResponse register(RegisterRequest request) {
        User user = new User();
        request.mapToEntity(user);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }

        Set<Role> roles;

        if (request.getRoles() == null || request.getRoles().isEmpty()) {

            Role userRole = roleRepository.findByRoleName("USER")
                    .orElseThrow(() -> new RuntimeException("USER role not found"));

            roles = Set.of(userRole);

        } else {

            roles = request.getRoles().stream()
                    .map(roleName -> roleRepository.findByRoleName(String.valueOf(roleName))
                            .orElseThrow(() ->
                                    new RuntimeException("Role not found: " + roleName)
                            ))
                    .collect(Collectors.toSet());
        }

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toResponse(userRepository.save(user));
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserOrPasswordNotFound("username or password not correct"));

        if (!verifyPassword(request.getPassword(), user.getPassword())) {
            throw new UserOrPasswordNotFound("username or password not correct");
        }

        String accessToken = jwtService.generateAccessToken(user.getId().toString(), user.getRole().toString());
        String refreshToken = refreshTokenService.generateRefreshToken(user.getId().toString(), user);


        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    @Transactional
    public String refreshAccessToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(token)
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshTokenService.isExpired(refreshToken)) {
            throw new RuntimeException("token expired");
        }

        if (!refreshTokenService.isValidToken(token)) {
            throw new RuntimeException("token is invalid");
        }
        User user = refreshToken.getUser();

        return "new accessToken:  " + jwtService.generateAccessToken(
                user.getId().toString(),
                user.getRole().toString());
    }


    private boolean verifyPassword(String requestPassword, String userPassword) {
        return passwordEncoder.matches(requestPassword, userPassword);
    }


    private UserResponse toResponse(User user) {
        UserResponse registerResponse = new UserResponse();
        registerResponse.setUsername(user.getUsername());
        registerResponse.setRole(user.getRole());
        return registerResponse;
    }
}
