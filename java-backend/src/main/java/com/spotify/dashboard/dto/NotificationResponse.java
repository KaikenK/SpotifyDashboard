package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String message;
    private Long artistId;
    private String artistUsername;
    private Long songId;
    private String songTitle;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse fromEntity(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getMessage(),
                n.getArtist().getId(),
                n.getArtist().getUsername(),
                n.getSong() == null ? null : n.getSong().getId(),
                n.getSong() == null ? null : n.getSong().getTitle(),
                n.getIsRead(),
                n.getCreatedAt()
        );
    }
}
