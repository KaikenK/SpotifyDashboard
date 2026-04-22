package com.spotify.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FanInsightResponse {
    private Long fanId;
    private String fanUsername;
    private Long totalInteractions;
    private Long playCount;
    private Long likeCount;
    private Long commentCount;
}
