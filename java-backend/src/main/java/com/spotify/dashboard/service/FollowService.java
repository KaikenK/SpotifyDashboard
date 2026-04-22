package com.spotify.dashboard.service;

import com.spotify.dashboard.exception.BadRequestException;
import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.Follow;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.FollowRepository;
import com.spotify.dashboard.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public Follow followArtist(User fan, Long artistId, boolean subscribe) {
        if (fan == null) {
            throw new BadRequestException("Authentication required");
        }
        if (fan.getId().equals(artistId)) {
            throw new BadRequestException("You cannot follow yourself");
        }

        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new NotFoundException("Artist not found"));
        if (artist.getRole() != User.Role.ARTIST) {
            throw new BadRequestException("Target user is not an artist");
        }

        Follow.FollowId id = new Follow.FollowId(fan.getId(), artistId);
        Follow follow = followRepository.findById(id).orElseGet(Follow::new);
        follow.setId(id);
        follow.setFan(fan);
        follow.setArtist(artist);
        follow.setIsSubscribed(subscribe);
        return followRepository.save(follow);
    }

    public Follow updateSubscription(User fan, Long artistId, boolean subscribe) {
        Follow.FollowId id = new Follow.FollowId(fan.getId(), artistId);
        Follow follow = followRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("You must follow the artist first"));
        follow.setIsSubscribed(subscribe);
        return followRepository.save(follow);
    }

    public List<Follow> getFanFollows(User fan) {
        return followRepository.findByFanId(fan.getId());
    }
}
