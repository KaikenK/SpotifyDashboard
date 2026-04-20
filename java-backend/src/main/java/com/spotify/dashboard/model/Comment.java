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
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    @ManyToOne
    @JoinColumn(name = "fan_id", nullable = false)
    private User fan;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_removed")
    private Boolean isRemoved = false;

    @ManyToOne
    @JoinColumn(name = "removed_by")
    private User removedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}