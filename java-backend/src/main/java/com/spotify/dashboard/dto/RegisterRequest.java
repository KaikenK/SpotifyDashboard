package com.spotify.dashboard.dto;

import com.spotify.dashboard.model.User;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private User.Role role;
}