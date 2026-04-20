package com.spotify.dashboard.service;

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

        if (userRepository.existsByEmail(email))
            throw new RuntimeException("Email already in use");
        if (userRepository.existsByUsername(username))
            throw new RuntimeException("Username already taken");

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
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, user.getPasswordHash()))
            throw new RuntimeException("Invalid password");

        if (!user.getIsVerified())
            throw new RuntimeException("Account pending admin verification");

        return jwtUtil.generateToken(user.getEmail(), user.getRole().name());
    }
}