package com.fnb.orderUsermanagementService.service.impl;

import com.fnb.orderUsermanagementService.dto.AuthResponse;
import com.fnb.orderUsermanagementService.dto.LoginRequest;
import com.fnb.orderUsermanagementService.dto.RegisterRequest;
import com.fnb.orderUsermanagementService.entity.User;
import com.fnb.orderUsermanagementService.kafka.UserEventProducer;
import com.fnb.orderUsermanagementService.repository.UserRepository;
import com.fnb.orderUsermanagementService.security.JwtUtil;
import com.fnb.orderUsermanagementService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UserEventProducer userEventProducer;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("USER")
                .build();

        userRepository.save(user);
        userEventProducer.sendUserRegisteredEvent(user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getUsername(), user.getEmail());
    }
}
