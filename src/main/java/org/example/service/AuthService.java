package org.example.service;

import org.example.database.entity.UserEntity;
import org.example.database.repository.UserRepository;
import org.example.dto.AuthResponse;
import org.example.dto.LoginRequest;
import org.example.dto.RegisterRequest;
import org.example.exception.EntityNotFoundException;
import org.example.security.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for authentication operations.
 * Handles user registration and login with JWT token generation.
 */
@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Register a new user.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + request.getUsername());
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        // Create and save user
        UserEntity user = new UserEntity();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole("ROLE_USER");

        UserEntity savedUser = userRepository.save(user);
        logger.info("User registered: username={}, email={}", savedUser.getUsername(), savedUser.getEmail());

        // Generate JWT token
        String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getRole());

        return new AuthResponse(
                token,
                jwtService.getExpirationSeconds(),
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    /**
     * Authenticate user and generate JWT token.
     */
    public AuthResponse login(LoginRequest request) {
        // Find user by username
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Invalid username or password"));

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new EntityNotFoundException("Invalid username or password");
        }

        logger.info("User logged in: username={}", user.getUsername());

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        return new AuthResponse(
                token,
                jwtService.getExpirationSeconds(),
                user.getUsername(),
                user.getRole()
        );
    }
}

