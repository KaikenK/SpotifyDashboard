package com.spotify.dashboard.service;

import com.spotify.dashboard.exception.ConflictException;
import com.spotify.dashboard.exception.UnauthorizedException;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.UserRepository;
import com.spotify.dashboard.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerFanAutoVerifiesAndPersistsEncodedPassword() {
        when(userRepository.existsByEmail("fan@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("fanuser")).thenReturn(false);
        when(passwordEncoder.encode("pw123")).thenReturn("encoded123");

        String result = authService.register("fanuser", "fan@example.com", "pw123", User.Role.FAN);

        assertEquals("Registered successfully", result);
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User savedUser = captor.getValue();
        assertEquals("encoded123", savedUser.getPasswordHash());
        assertTrue(savedUser.getIsVerified());
    }

    @Test
    void registerThrowsConflictForDuplicateEmail() {
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> authService.register("fanuser", "taken@example.com", "pw123", User.Role.FAN));
    }

    @Test
    void loginReturnsTokenForVerifiedCredentials() {
        User user = new User();
        user.setEmail("fan@example.com");
        user.setPasswordHash("encoded123");
        user.setRole(User.Role.FAN);
        user.setIsVerified(true);

        when(userRepository.findByEmail("fan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pw123", "encoded123")).thenReturn(true);
        when(jwtUtil.generateToken("fan@example.com", "FAN")).thenReturn("jwt-token");

        String token = authService.login("fan@example.com", "pw123");

        assertEquals("jwt-token", token);
    }

    @Test
    void loginThrowsUnauthorizedForInvalidPassword() {
        User user = new User();
        user.setEmail("fan@example.com");
        user.setPasswordHash("encoded123");
        user.setRole(User.Role.FAN);
        user.setIsVerified(true);

        when(userRepository.findByEmail("fan@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "encoded123")).thenReturn(false);

        assertThrows(UnauthorizedException.class,
                () -> authService.login("fan@example.com", "bad"));
    }
}