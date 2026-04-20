package com.spotify.dashboard.repository;

import com.spotify.dashboard.model.Follow;
import com.spotify.dashboard.model.Follow.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findByArtistId(Long artistId);
    List<Follow> findByFanId(Long fanId);
    List<Follow> findByArtistIdAndIsSubscribedTrue(Long artistId);
    boolean existsByFanIdAndArtistId(Long fanId, Long artistId);
}