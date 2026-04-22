package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.AuthTokenResponse;
import com.spotify.dashboard.dto.LoginRequest;
import com.spotify.dashboard.dto.MessageResponse;
import com.spotify.dashboard.dto.RegisterRequest;
import com.spotify.dashboard.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@RequestBody RegisterRequest request) {
        String message = authService.register(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
        );
        return ResponseEntity.ok(new MessageResponse(message));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthTokenResponse(token));
    }
}