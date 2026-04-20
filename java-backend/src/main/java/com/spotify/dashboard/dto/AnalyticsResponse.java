package com.spotify.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private Long songId;
    private String songTitle;
    private Long totalPlays;
    private Long totalLikes;
    private Long totalComments;
    private Long totalSaves;
    private Double engagementScore;
}