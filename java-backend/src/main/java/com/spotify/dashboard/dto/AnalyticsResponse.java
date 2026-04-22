package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.SongAnalytics;
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

    public static AnalyticsResponse fromEntity(SongAnalytics analytics) {
        return new AnalyticsResponse(
                analytics.getSong().getId(),
                analytics.getSong().getTitle(),
                analytics.getTotalPlays(),
                analytics.getTotalLikes(),
                analytics.getTotalComments(),
                analytics.getTotalSaves(),
                analytics.getEngagementScore()
        );
    }
}