package com.spotify.dashboard.repository;

import com.spotify.dashboard.model.SongAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SongAnalyticsRepository extends JpaRepository<SongAnalytics, Long> {
    Optional<SongAnalytics> findBySongId(Long songId);
}