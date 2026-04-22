package com.spotify.dashboard.service;

import com.spotify.dashboard.exception.BadRequestException;
import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.Comment;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.CommentRepository;
import com.spotify.dashboard.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final SongRepository songRepository;
    private final AnalyticsService analyticsService;

    public Comment createComment(User fan, Long songId, String content) {
        if (fan == null) {
            throw new BadRequestException("Authentication required to comment");
        }
        if (content == null || content.isBlank()) {
            throw new BadRequestException("Comment content is required");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new NotFoundException("Song not found"));

        Comment comment = new Comment();
        comment.setSong(song);
        comment.setFan(fan);
        comment.setContent(content.trim());
        Comment saved = commentRepository.save(comment);

        analyticsService.recordComment(songId, fan);
        return saved;
    }

    public List<Comment> getSongComments(Long songId) {
        return commentRepository.findBySongIdAndIsRemovedFalse(songId);
    }

    public Comment moderateComment(Long commentId, User admin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.setIsRemoved(true);
        comment.setRemovedBy(admin);
        return commentRepository.save(comment);
    }

    public List<Comment> getAllActiveComments() {
        return commentRepository.findAll().stream()
                .filter(comment -> !Boolean.TRUE.equals(comment.getIsRemoved()))
                .toList();
    }
}
