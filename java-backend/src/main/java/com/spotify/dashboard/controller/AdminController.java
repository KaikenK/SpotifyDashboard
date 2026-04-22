package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.MessageResponse;
import com.spotify.dashboard.dto.SystemReportResponse;
import com.spotify.dashboard.dto.UserSummaryResponse;
import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.repository.UserRepository;
import com.spotify.dashboard.repository.CommentRepository;
import com.spotify.dashboard.repository.FollowRepository;
import com.spotify.dashboard.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final CommentRepository commentRepository;
    private final FollowRepository followRepository;

    @GetMapping("/unverified-artists")
    public ResponseEntity<List<UserSummaryResponse>> getUnverifiedArtists() {
        return ResponseEntity.ok(userRepository.findByRoleAndIsVerifiedFalse(User.Role.ARTIST).stream()
                .map(UserSummaryResponse::fromEntity)
                .toList());
    }

    @PutMapping("/verify-artist/{userId}")
    public ResponseEntity<MessageResponse> verifyArtist(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setIsVerified(true);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User verified successfully"));
    }

    @GetMapping("/reports/system")
    public ResponseEntity<SystemReportResponse> generateSystemReport() {
        long totalUsers = userRepository.count();
        long totalArtists = userRepository.findAll().stream().filter(u -> u.getRole() == User.Role.ARTIST).count();
        long totalFans = userRepository.findAll().stream().filter(u -> u.getRole() == User.Role.FAN).count();
        long totalSongs = songRepository.count();
        long publishedSongs = songRepository.findByStatus(Song.SongStatus.PUBLISHED).size();
        long pendingSongs = songRepository.findByStatus(Song.SongStatus.PENDING_APPROVAL).size();
        long totalComments = commentRepository.count();
        long activeSubscriptions = followRepository.findAll().stream()
                .filter(follow -> Boolean.TRUE.equals(follow.getIsSubscribed()))
                .count();

        return ResponseEntity.ok(new SystemReportResponse(
                totalUsers,
                totalArtists,
                totalFans,
                totalSongs,
                publishedSongs,
                pendingSongs,
                totalComments,
                activeSubscriptions
        ));
    }
}