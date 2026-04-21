package com.spotify.dashboard.service;

import com.spotify.dashboard.model.SongAnalytics;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.repository.SongAnalyticsRepository;
import com.spotify.dashboard.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SongAnalyticsRepository analyticsRepository;
    private final SongRepository songRepository;

    @Transactional
    public void recordPlay(Long songId) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalPlays(analytics.getTotalPlays() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);
    }

    @Transactional
    public void recordLike(Long songId) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalLikes(analytics.getTotalLikes() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);
    }

    @Transactional
    public void recordComment(Long songId) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalComments(analytics.getTotalComments() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);
    }

    public SongAnalytics getSongAnalytics(Long songId) {
        return analyticsRepository.findBySongId(songId)
                .orElseThrow(() -> new RuntimeException("Analytics not found"));
    }

    public List<SongAnalytics> getArtistAnalytics(Long artistId) {
        List<Song> songs = songRepository.findByArtistId(artistId);
        return songs.stream()
                .map(song -> analyticsRepository.findBySongId(song.getId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .toList();
    }

    private void recalculateEngagement(SongAnalytics a) {
        // Weighted engagement score formula
        double score = (a.getTotalPlays() * 1.0)
                + (a.getTotalLikes() * 3.0)
                + (a.getTotalComments() * 5.0)
                + (a.getTotalSaves() * 4.0);
        a.setEngagementScore(Math.min(score / 100.0, 100.0));
    }

    private SongAnalytics getOrCreate(Long songId) {
        return analyticsRepository.findBySongId(songId)
                .orElseGet(() -> {
                    try {
                        Song song = songRepository.findById(songId)
                                .orElseThrow(() -> new RuntimeException("Song not found"));
                        SongAnalytics analytics = new SongAnalytics();
                        analytics.setSong(song);
                        return analyticsRepository.save(analytics);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        // Prevent race condition duplicate creation
                        return analyticsRepository.findBySongId(songId)
                                .orElseThrow(() -> new RuntimeException("Wait conflict error"));
                    }
                });
    }
}