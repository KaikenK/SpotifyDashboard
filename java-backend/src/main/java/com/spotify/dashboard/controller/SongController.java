package com.spotify.dashboard.controller;

import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    // Public — any visitor can see published songs
    @GetMapping("/published")
    public ResponseEntity<List<Song>> getPublishedSongs() {
        return ResponseEntity.ok(songService.getPublishedSongs());
    }

    // Artist — upload a new song
    @PostMapping("/upload")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<?> uploadSong(
            @AuthenticationPrincipal User artist,
            @RequestParam String title,
            @RequestParam Integer durationSec) {
        Song song = songService.uploadSong(artist, title, durationSec, null);
        return ResponseEntity.ok(song);
    }

    // Artist — submit song for admin approval
    @PutMapping("/{id}/submit")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<?> submitSong(@PathVariable Long id) {
        return ResponseEntity.ok(songService.submitForApproval(id));
    }

    // Artist — get all their own songs
    @GetMapping("/my")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<List<Song>> getMySongs(
            @AuthenticationPrincipal User artist) {
        return ResponseEntity.ok(songService.getArtistSongs(artist.getId()));
    }

    // Admin — get all songs pending approval
    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Song>> getPendingSongs() {
        return ResponseEntity.ok(songService.getPendingSongs());
    }

    // Admin — approve a song
    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> approveSong(@PathVariable Long id) {
        return ResponseEntity.ok(songService.approveSong(id));
    }

    // Admin — reject a song
    @PutMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> rejectSong(@PathVariable Long id) {
        return ResponseEntity.ok(songService.rejectSong(id));
    }

    // Admin — unpublish a song
    @PutMapping("/{id}/unpublish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> unpublishSong(@PathVariable Long id) {
        return ResponseEntity.ok(songService.unpublishSong(id));
    }
}