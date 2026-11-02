package com.scholarship.finder.service;

import com.scholarship.finder.config.JwtTokenProvider;
import com.scholarship.finder.dto.AuthRequest;
import com.scholarship.finder.dto.AuthResponse;
import com.scholarship.finder.dto.RegisterRequest;
import com.scholarship.finder.entity.User;
import com.scholarship.finder.exception.BadRequestException;
import com.scholarship.finder.repository.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = new User();
        user.setName(request.getName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null && request.getRole().equalsIgnoreCase("ADMIN") ? "ADMIN" : "USER");
        user.setPreferredLanguage(request.getPreferredLanguage() != null ? request.getPreferredLanguage() : "en");

        User savedUser = userRepository.save(user);

        String token = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getRole(), savedUser.getId());
        return new AuthResponse(token, savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole(), savedUser.getPreferredLanguage());
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole(), user.getId());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getPreferredLanguage());
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }
}

