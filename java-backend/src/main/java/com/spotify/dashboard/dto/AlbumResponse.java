package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.Album;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumResponse {
    private Long id;
    private Long artistId;
    private String title;
    private String coverArt;
    private LocalDate releaseDate;
    private LocalDateTime createdAt;

    public static AlbumResponse fromEntity(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getArtist().getId(),
                album.getTitle(),
                album.getCoverArt(),
                album.getReleaseDate(),
                album.getCreatedAt()
        );
    }
}
