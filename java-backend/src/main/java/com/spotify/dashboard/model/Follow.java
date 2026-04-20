package com.spotify.dashboard.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "follows")
public class Follow {

    @EmbeddedId
    private FollowId id = new FollowId();

    @ManyToOne
    @MapsId("fanId")
    @JoinColumn(name = "fan_id")
    private User fan;

    @ManyToOne
    @MapsId("artistId")
    @JoinColumn(name = "artist_id")
    private User artist;

    @Column(name = "is_subscribed")
    private Boolean isSubscribed = false;

    @Column(name = "followed_at")
    private LocalDateTime followedAt = LocalDateTime.now();

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FollowId implements java.io.Serializable {
        private Long fanId;
        private Long artistId;
    }
}