package com.spotify.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendPointResponse {
    private String date;
    private Long plays;
    private Long likes;
    private Long comments;
    private Double engagement;
}
