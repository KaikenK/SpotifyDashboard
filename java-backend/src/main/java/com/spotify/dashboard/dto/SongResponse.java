package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.Song;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongResponse {
    private Long id;
    private String title;
    private Integer durationSec;
    private String coverArt;
    private String status;
    private Long artistId;
    private String artistUsername;
    private Long albumId;
    private LocalDateTime uploadedAt;
    private LocalDateTime publishedAt;

    public static SongResponse fromEntity(Song song) {
        return new SongResponse(
                song.getId(),
                song.getTitle(),
                song.getDurationSec(),
                song.getCoverArt(),
                song.getStatus() == null ? null : song.getStatus().name(),
                song.getArtist() == null ? null : song.getArtist().getId(),
                song.getArtist() == null ? null : song.getArtist().getUsername(),
                song.getAlbum() == null ? null : song.getAlbum().getId(),
                song.getUploadedAt(),
                song.getPublishedAt()
        );
    }
}