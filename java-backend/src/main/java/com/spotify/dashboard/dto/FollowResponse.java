package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.Follow;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowResponse {
    private Long fanId;
    private Long artistId;
    private String artistUsername;
    private Boolean isSubscribed;
    private LocalDateTime followedAt;

    public static FollowResponse fromEntity(Follow follow) {
        return new FollowResponse(
                follow.getFan().getId(),
                follow.getArtist().getId(),
                follow.getArtist().getUsername(),
                follow.getIsSubscribed(),
                follow.getFollowedAt()
        );
    }
}
