package com.example.TicketSupport.service;

import com.example.TicketSupport.dto.LoginRequest;
import com.example.TicketSupport.dto.RegisterRequest;
import com.example.TicketSupport.dto.UserResponse;
import com.example.TicketSupport.entity.User;
import com.example.TicketSupport.exception.UserOrPasswordNotFound;
import com.example.TicketSupport.exception.UsernameAlreadyExistsException;
import com.example.TicketSupport.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    @Transactional
    public UserResponse register(RegisterRequest request){
        User user = new User();
        request.mapToEntity(user);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(user.getUsername());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return toResponse(userRepository.save(user));
    }


    public String login(LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserOrPasswordNotFound("username or password not correct"));

        if(!verifyPassword(request.getPassword(), user.getPassword())) {
            throw new UserOrPasswordNotFound("username or password not correct");
        }

        return jwtService.generateJwt(user.getUsername(),user.getRole().name());
    }

    private boolean verifyPassword(String requestPassword, String userPassword){
        return passwordEncoder.matches(requestPassword, userPassword);
    }

    private UserResponse toResponse(User user){
        UserResponse registerResponse = new UserResponse();
        registerResponse.setUsername(user.getUsername());
        registerResponse.setRole(user.getRole());
        return registerResponse;
    }
}
