package com.spotify.dashboard.controller;

import com.spotify.dashboard.dto.CommentRequest;
import com.spotify.dashboard.dto.CommentResponse;
import com.spotify.dashboard.dto.MessageResponse;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/song/{songId}")
    public ResponseEntity<List<CommentResponse>> getSongComments(@PathVariable Long songId) {
        return ResponseEntity.ok(commentService.getSongComments(songId).stream()
                .map(CommentResponse::fromEntity)
                .toList());
    }

    @PostMapping("/song/{songId}")
    @PreAuthorize("hasRole('FAN')")
    public ResponseEntity<CommentResponse> addComment(
            @AuthenticationPrincipal User fan,
            @PathVariable Long songId,
            @RequestBody CommentRequest request) {
        return ResponseEntity.ok(CommentResponse.fromEntity(
                commentService.createComment(fan, songId, request.getContent())));
    }

    @GetMapping("/moderation")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CommentResponse>> getModerationQueue() {
        return ResponseEntity.ok(commentService.getAllActiveComments().stream()
                .map(CommentResponse::fromEntity)
                .toList());
    }

    @PutMapping("/{commentId}/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> removeComment(
            @AuthenticationPrincipal User admin,
            @PathVariable Long commentId) {
        commentService.moderateComment(commentId, admin);
        return ResponseEntity.ok(new MessageResponse("Comment removed"));
    }
}
