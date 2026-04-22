package com.spotify.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumAnalyticsResponse {
    private Long albumId;
    private String albumTitle;
    private Long totalSongs;
    private Long totalPlays;
    private Long totalLikes;
    private Long totalComments;
    private Long totalSaves;
    private Double averageEngagementScore;
}
