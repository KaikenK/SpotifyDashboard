package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.AlbumAnalyticsResponse;
import com.spotify.dashboard.dto.AnalyticsResponse;
import com.spotify.dashboard.dto.FanInsightResponse;
import com.spotify.dashboard.dto.MessageResponse;
import com.spotify.dashboard.dto.TrendPointResponse;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.AlbumService;
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
    private final AlbumService albumService;

    // Artist — get analytics for a specific song
    @GetMapping("/song/{songId}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<AnalyticsResponse> getSongAnalytics(
            @PathVariable Long songId) {
        return ResponseEntity.ok(AnalyticsResponse.fromEntity(
            analyticsService.getSongAnalytics(songId)));
    }

    // Artist — get analytics for all their songs
    @GetMapping("/my")
    @PreAuthorize("hasRole('ARTIST')")
        public ResponseEntity<List<AnalyticsResponse>> getMyAnalytics(
            @AuthenticationPrincipal User artist) {
        return ResponseEntity.ok(
            analyticsService.getArtistAnalytics(artist.getId()).stream()
                .map(AnalyticsResponse::fromEntity)
                .toList());
    }

        @GetMapping("/album/{albumId}")
        @PreAuthorize("hasRole('ARTIST')")
        public ResponseEntity<AlbumAnalyticsResponse> getAlbumAnalytics(
                @AuthenticationPrincipal User artist,
                @PathVariable Long albumId) {
            return ResponseEntity.ok(albumService.getAlbumAnalytics(artist, albumId));
        }

        @GetMapping("/fan-insights")
        @PreAuthorize("hasRole('ARTIST')")
        public ResponseEntity<List<FanInsightResponse>> getFanInsights(
                @AuthenticationPrincipal User artist,
                @RequestParam(defaultValue = "10") int limit) {
            return ResponseEntity.ok(analyticsService.getFanInsights(artist.getId(), limit));
        }

        @GetMapping("/trend")
        @PreAuthorize("hasRole('ARTIST')")
        public ResponseEntity<List<TrendPointResponse>> getTrend(
                @AuthenticationPrincipal User artist,
                @RequestParam(defaultValue = "14") int days) {
            return ResponseEntity.ok(analyticsService.getGrowthTrend(artist.getId(), days));
        }

        @GetMapping("/song/{songId}/replay-heatmap")
        @PreAuthorize("hasRole('ARTIST')")
        public ResponseEntity<List<Long>> getReplayHeatmap(@PathVariable Long songId) {
            return ResponseEntity.ok(analyticsService.getReplayTimestamps(songId));
        }

    // Fan — record a play event
    @PostMapping("/play/{songId}")
        public ResponseEntity<MessageResponse> recordPlay(
                @PathVariable Long songId,
                @AuthenticationPrincipal User fan) {
            analyticsService.recordPlay(songId, fan);
        return ResponseEntity.ok(new MessageResponse("Play recorded"));
    }

    // Fan — record a like event
    @PostMapping("/like/{songId}")
        public ResponseEntity<MessageResponse> recordLike(
                @PathVariable Long songId,
                @AuthenticationPrincipal User fan) {
            analyticsService.recordLike(songId, fan);
        return ResponseEntity.ok(new MessageResponse("Like recorded"));
    }

    @PostMapping("/save/{songId}")
    public ResponseEntity<MessageResponse> recordSave(
            @PathVariable Long songId,
            @AuthenticationPrincipal User fan) {
        analyticsService.recordSave(songId, fan);
        return ResponseEntity.ok(new MessageResponse("Save recorded"));
    }

    @PostMapping("/replay/{songId}")
    public ResponseEntity<MessageResponse> recordReplay(
            @PathVariable Long songId,
            @AuthenticationPrincipal User fan) {
        analyticsService.recordReplay(songId, fan);
        return ResponseEntity.ok(new MessageResponse("Replay recorded"));
    }
}