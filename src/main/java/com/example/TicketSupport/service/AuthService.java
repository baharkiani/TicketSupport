package com.example.TicketSupport.service;

import com.example.TicketSupport.dto.*;
import com.example.TicketSupport.entity.*;
import com.example.TicketSupport.exception.AccountLockException;
import com.example.TicketSupport.exception.InvalidRefreshTokenException;
import com.example.TicketSupport.exception.UserOrPasswordNotFound;
import com.example.TicketSupport.exception.UsernameAlreadyExistsException;
import com.example.TicketSupport.repository.*;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final RevokedTokenRepository revokedTokenRepository;
    private final DepartmentRepository  departmentRepository;


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


        Department department = departmentRepository
                .findByName(request.getDepartment())
                .orElseThrow(() ->
                        new IllegalArgumentException("Department not found"));

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setDepartment(department);
        return toResponse(userRepository.save(user));
    }

    @Transactional(noRollbackFor = {AccountLockException.class,
            UserOrPasswordNotFound.class})
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserOrPasswordNotFound("username or password not correct"));

        if (user.isUserLocked()) {
            throw new AccountLockException("for 15 minutes");
        }

        if (!verifyPassword(request.getPassword(), user.getPassword())) {
            user.increseLoginAttempts();
            if (user.getLoginAttempts() >= 3) {
                user.lockUser();
            }
            userRepository.save(user);
            throw new UserOrPasswordNotFound("Username or password not correct");
        }

        user.resetLoginAttemps();
        userRepository.save(user);

        String roles = user.getRoles()
                .stream()
                .map(Role::getRoleName)
                .collect(Collectors.joining(","));
        String accessToken = jwtService.generateAccessToken(user.getId(), roles, user.getUsername().toString());
        String refreshToken = refreshTokenService.generateRefreshToken(user.getId().toString(), user);


        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        return response;
    }

    @Transactional
    public String refreshAccessToken(String token) {

        RefreshToken refreshToken =
                refreshTokenRepository.findByToken(refreshTokenService.hashToken(token))
                        .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshTokenService.isExpired(refreshToken)) {
            throw new RuntimeException("token expired");
        }

        if (!refreshTokenService.isValidToken(token)) {
            throw new RuntimeException("token is invalid");
        }
        User user = refreshToken.getUser();

        return "new accessToken:  " + jwtService.generateAccessToken(
                user.getId(),
                user.getRoles().toString(),
                user.getUsername().toString());
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Transactional
    public void updatePassword(PasswordUpdateRequest request, Authentication authentication) {

        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UserOrPasswordNotFound("username or password not found!"));

        if (!verifyPassword(request.getOldPassword(), user.getPassword())) {
            throw new UserOrPasswordNotFound("username or password not correct");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

    }

    @Transactional
    public void logout(
            LogoutRequest request,
            HttpServletRequest httpRequest
    ) {

        // 1. Access Token
        Claims claims =
                (Claims) httpRequest.getAttribute("jwtClaims");

        String jti = claims.getId();
        Instant expiryAt =
                claims.getExpiration().toInstant();

        // 2. Revoke Access Token
        RevokedToken revokedToken =
                new RevokedToken(jti, expiryAt);

        revokedTokenRepository.save(revokedToken);

        // 3. Delete Refresh Token
        String refreshToken = request.getRefreshToken();

        RefreshToken token = refreshTokenRepository
                .findByToken(refreshTokenService.hashToken(refreshToken))
                .orElseThrow(() ->
                        new InvalidRefreshTokenException(
                                "Token is not valid"
                        ));

        refreshTokenRepository.deleteByToken(token.getToken());
    }


    @Transactional
    public void unlockUser(UnlockUserRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UserOrPasswordNotFound("username invalid"));
        user.resetLoginAttemps();
        userRepository.save(user);
    }











    private boolean verifyPassword(String requestPassword, String userPassword) {
        return passwordEncoder.matches(requestPassword, userPassword);
    }


    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setUsername(user.getUsername());

        // فقط نام نقش‌ها را برگردان (نه کل Entity)
        response.setRole(
                user.getRoles()
                        .stream()
                        .map(role -> role.getRoleName())
                        .toList()
        );
        return response;
    }


}
