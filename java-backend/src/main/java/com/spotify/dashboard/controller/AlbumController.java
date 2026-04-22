package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.AlbumAnalyticsResponse;
import com.spotify.dashboard.dto.AlbumRequest;
import com.spotify.dashboard.dto.AlbumResponse;
import com.spotify.dashboard.dto.MessageResponse;
import com.spotify.dashboard.dto.SongResponse;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ARTIST')")
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<AlbumResponse> createAlbum(
            @AuthenticationPrincipal User artist,
            @RequestBody AlbumRequest request) {
        return ResponseEntity.ok(AlbumResponse.fromEntity(
                albumService.createAlbum(artist, request.getTitle(), request.getCoverArt(), request.getReleaseDate())));
    }

    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumResponse> updateAlbum(
            @AuthenticationPrincipal User artist,
            @PathVariable Long albumId,
            @RequestBody AlbumRequest request) {
        return ResponseEntity.ok(AlbumResponse.fromEntity(
                albumService.updateAlbum(artist, albumId, request.getTitle(), request.getCoverArt(), request.getReleaseDate())));
    }

    @PutMapping("/{albumId}/songs/{songId}")
    public ResponseEntity<SongResponse> addSongToAlbum(
            @AuthenticationPrincipal User artist,
            @PathVariable Long albumId,
            @PathVariable Long songId) {
        return ResponseEntity.ok(SongResponse.fromEntity(
                albumService.addSongToAlbum(artist, albumId, songId)));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AlbumResponse>> getMyAlbums(@AuthenticationPrincipal User artist) {
        return ResponseEntity.ok(albumService.getArtistAlbums(artist).stream()
                .map(AlbumResponse::fromEntity)
                .toList());
    }

    @GetMapping("/{albumId}/analytics")
    public ResponseEntity<AlbumAnalyticsResponse> getAlbumAnalytics(
            @AuthenticationPrincipal User artist,
            @PathVariable Long albumId) {
        return ResponseEntity.ok(albumService.getAlbumAnalytics(artist, albumId));
    }

    @PutMapping("/{albumId}/publish")
    public ResponseEntity<MessageResponse> publishAlbumPlaceholder(@PathVariable Long albumId) {
        return ResponseEntity.ok(new MessageResponse("Album publishing is handled through song publish workflow"));
    }
}
