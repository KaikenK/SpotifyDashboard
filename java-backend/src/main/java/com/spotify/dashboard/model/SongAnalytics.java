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
@Table(name = "song_analytics")
public class SongAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @Column(name = "total_plays")
    private Long totalPlays = 0L;

    @Column(name = "total_likes")
    private Long totalLikes = 0L;

    @Column(name = "total_comments")
    private Long totalComments = 0L;

    @Column(name = "total_saves")
    private Long totalSaves = 0L;

    @Column(name = "engagement_score")
    private Double engagementScore = 0.0;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}