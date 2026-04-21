package com.spotify.dashboard.controller;

import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;

    @GetMapping("/unverified-artists")
    public ResponseEntity<List<User>> getUnverifiedArtists() {
        return ResponseEntity.ok(userRepository.findByRoleAndIsVerifiedFalse(User.Role.ARTIST));
    }

    @PutMapping("/verify-artist/{userId}")
    public ResponseEntity<?> verifyArtist(@PathVariable Long userId) {
        return userRepository.findById(userId).map(user -> {
            user.setIsVerified(true);
            userRepository.save(user);
            return ResponseEntity.ok(Map.of("message", "User verified successfully"));
        }).orElse(ResponseEntity.notFound().build());
    }
}