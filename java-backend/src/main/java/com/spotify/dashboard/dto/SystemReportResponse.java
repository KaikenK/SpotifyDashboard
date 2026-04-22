package com.spotify.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemReportResponse {
    private Long totalUsers;
    private Long totalArtists;
    private Long totalFans;
    private Long totalSongs;
    private Long publishedSongs;
    private Long pendingSongs;
    private Long totalComments;
    private Long activeSubscriptions;
}
