package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Boolean isVerified;
    private LocalDateTime createdAt;

    public static UserSummaryResponse fromEntity(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole() == null ? null : user.getRole().name(),
                user.getIsVerified(),
                user.getCreatedAt()
        );
    }
}