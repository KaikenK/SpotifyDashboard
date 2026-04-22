package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.SongResponse;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    // Public — any visitor can see published songs
    @GetMapping("/published")
    public ResponseEntity<List<SongResponse>> getPublishedSongs() {
        return ResponseEntity.ok(songService.getPublishedSongs().stream()
                .map(SongResponse::fromEntity)
                .toList());
    }

    // Artist — upload a new song
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<SongResponse> uploadSong(
            @AuthenticationPrincipal User artist,
            @RequestParam String title,
            @RequestParam Integer durationSec) {
        Song song = songService.uploadSong(artist, title, durationSec, null);
        return ResponseEntity.ok(SongResponse.fromEntity(song));
    }

    // Artist — submit song for admin approval
    @PutMapping("/{id}/submit")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<SongResponse> submitSong(@PathVariable Long id) {
        return ResponseEntity.ok(SongResponse.fromEntity(songService.submitForApproval(id)));
    }

    // Artist — get all their own songs
    @GetMapping("/my")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<List<SongResponse>> getMySongs(
            @AuthenticationPrincipal User artist) {
        return ResponseEntity.ok(songService.getArtistSongs(artist.getId()).stream()
                .map(SongResponse::fromEntity)
                .toList());
    }

    // Admin — get all songs pending approval
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SongResponse>> getPendingSongs() {
        return ResponseEntity.ok(songService.getPendingSongs().stream()
                .map(SongResponse::fromEntity)
                .toList());
    }

    // Admin — approve a song
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongResponse> approveSong(@PathVariable Long id) {
        return ResponseEntity.ok(SongResponse.fromEntity(songService.approveSong(id)));
    }

    // Admin — reject a song
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongResponse> rejectSong(@PathVariable Long id) {
        return ResponseEntity.ok(SongResponse.fromEntity(songService.rejectSong(id)));
    }

    // Admin — unpublish a song
    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SongResponse> unpublishSong(@PathVariable Long id) {
        return ResponseEntity.ok(SongResponse.fromEntity(songService.unpublishSong(id)));
    }
}