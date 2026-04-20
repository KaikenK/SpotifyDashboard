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
@Table(name = "songs")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    private User artist;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "duration_sec")
    private Integer durationSec = 0;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "cover_art")
    private String coverArt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SongStatus status = SongStatus.UPLOADED;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt = LocalDateTime.now();

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum SongStatus {
        UPLOADED, PENDING_APPROVAL, APPROVED,
        PUBLISHED, ACTIVE, REJECTED, UNPUBLISHED
    }
}