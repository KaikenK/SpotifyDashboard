package com.spotify.dashboard.service;

import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.Notification;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<Notification> getMyNotifications(User user) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(user.getId());
    }

    public Notification markAsRead(User user, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
        if (!notification.getRecipient().getId().equals(user.getId())) {
            throw new NotFoundException("Notification not found for current user");
        }
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }
}
