package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long id;
    private Long songId;
    private Long fanId;
    private String fanUsername;
    private String content;
    private Boolean isRemoved;
    private LocalDateTime createdAt;

    public static CommentResponse fromEntity(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getSong().getId(),
                comment.getFan().getId(),
                comment.getFan().getUsername(),
                comment.getContent(),
                comment.getIsRemoved(),
                comment.getCreatedAt()
        );
    }
}
