package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.NotificationResponse;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(notificationService.getMyNotifications(user).stream()
                .map(NotificationResponse::fromEntity)
                .toList());
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @AuthenticationPrincipal User user,
            @PathVariable Long notificationId) {
        return ResponseEntity.ok(NotificationResponse.fromEntity(
                notificationService.markAsRead(user, notificationId)));
    }
}
