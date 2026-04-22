package com.spotify.dashboard.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AlbumRequest {
    private String title;
    private String coverArt;
    private LocalDate releaseDate;
}
