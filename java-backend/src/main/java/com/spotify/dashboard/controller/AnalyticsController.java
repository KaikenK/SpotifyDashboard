package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.AnalyticsResponse;
import com.spotify.dashboard.model.SongAnalytics;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // Artist — get analytics for a specific song
    @GetMapping("/song/{songId}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<AnalyticsResponse> getSongAnalytics(
            @PathVariable Long songId) {
        SongAnalytics a = analyticsService.getSongAnalytics(songId);
        AnalyticsResponse response = new AnalyticsResponse(
                a.getSong().getId(),
                a.getSong().getTitle(),
                a.getTotalPlays(),
                a.getTotalLikes(),
                a.getTotalComments(),
                a.getTotalSaves(),
                a.getEngagementScore()
        );
        return ResponseEntity.ok(response);
    }

    // Artist — get analytics for all their songs
    @GetMapping("/my")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<List<SongAnalytics>> getMyAnalytics(
            @AuthenticationPrincipal User artist) {
        return ResponseEntity.ok(
                analyticsService.getArtistAnalytics(artist.getId()));
    }

    // Fan — record a play event
    @PostMapping("/play/{songId}")
    public ResponseEntity<?> recordPlay(@PathVariable Long songId) {
        analyticsService.recordPlay(songId);
        return ResponseEntity.ok("Play recorded");
    }

    // Fan — record a like event
    @PostMapping("/like/{songId}")
    public ResponseEntity<?> recordLike(@PathVariable Long songId) {
        analyticsService.recordLike(songId);
        return ResponseEntity.ok("Like recorded");
    }
}