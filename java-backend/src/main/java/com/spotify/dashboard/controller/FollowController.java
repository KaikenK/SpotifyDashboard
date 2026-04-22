package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.FollowResponse;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
@PreAuthorize("hasRole('FAN')")
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{artistId}")
    public ResponseEntity<FollowResponse> followArtist(
            @AuthenticationPrincipal User fan,
            @PathVariable Long artistId,
            @RequestParam(defaultValue = "false") boolean subscribe) {
        return ResponseEntity.ok(FollowResponse.fromEntity(
                followService.followArtist(fan, artistId, subscribe)));
    }

    @PutMapping("/{artistId}/subscription")
    public ResponseEntity<FollowResponse> updateSubscription(
            @AuthenticationPrincipal User fan,
            @PathVariable Long artistId,
            @RequestParam boolean subscribe) {
        return ResponseEntity.ok(FollowResponse.fromEntity(
                followService.updateSubscription(fan, artistId, subscribe)));
    }

    @GetMapping("/my")
    public ResponseEntity<List<FollowResponse>> getMyFollows(@AuthenticationPrincipal User fan) {
        return ResponseEntity.ok(followService.getFanFollows(fan).stream()
                .map(FollowResponse::fromEntity)
                .toList());
    }
}
