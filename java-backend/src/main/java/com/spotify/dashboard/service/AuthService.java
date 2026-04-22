package com.spotify.dashboard.service;

import com.spotify.dashboard.exception.BadRequestException;
import com.spotify.dashboard.exception.ConflictException;
import com.spotify.dashboard.exception.UnauthorizedException;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.UserRepository;
import com.spotify.dashboard.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String register(String username, String email,
                           String password, User.Role role) {

        if (role == null) {
            throw new BadRequestException("Role is required");
        }

        if (userRepository.existsByEmail(email))
            throw new ConflictException("Email already in use");
        if (userRepository.existsByUsername(username))
            throw new ConflictException("Username already taken");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsVerified(role == User.Role.FAN); // fans auto-verified

        userRepository.save(user);
        return "Registered successfully";
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new UnauthorizedException("Invalid credentials");

        if (!user.getIsVerified())
            throw new UnauthorizedException("Account pending admin verification");

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }
}