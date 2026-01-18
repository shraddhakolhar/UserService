package com.scaler.userservice.service;

import com.scaler.userservice.dto.AuthResponse;
import com.scaler.userservice.dto.LoginRequest;
import com.scaler.userservice.dto.RegisterRequest;
import com.scaler.userservice.entity.UserEntity;
import com.scaler.userservice.event.UserRegisteredEvent;
import com.scaler.userservice.repository.UserRepository;
import com.scaler.userservice.security.JwtUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.kafkaTemplate = kafkaTemplate;
    }

    // Register a new user
    @Transactional
    public void register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // Persist user
        userRepository.save(user);

        // ===============================
        // Publish Kafka Event (USER_REGISTERED)
        // ===============================
        UserRegisteredEvent.Payload payload =
                new UserRegisteredEvent.Payload(
                        user.getFirstName(),
                        user.getLastName()
                );

        UserRegisteredEvent event =
                new UserRegisteredEvent(
                        "USER_REGISTERED",
                        user.getId(),
                        user.getEmail(),
                        payload
                );

        kafkaTemplate.send("notification-events", event);
    }

    // Login user and generate JWT token
    public AuthResponse login(LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())
        ) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}
