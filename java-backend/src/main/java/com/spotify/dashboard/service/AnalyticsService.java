package com.spotify.dashboard.service;

import com.spotify.dashboard.dto.FanInsightResponse;
import com.spotify.dashboard.dto.TrendPointResponse;
import com.spotify.dashboard.exception.ConflictException;
import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.InteractionEvent;
import com.spotify.dashboard.model.SongAnalytics;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.InteractionEventRepository;
import com.spotify.dashboard.repository.SongAnalyticsRepository;
import com.spotify.dashboard.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final SongAnalyticsRepository analyticsRepository;
    private final SongRepository songRepository;
    private final InteractionEventRepository interactionEventRepository;

    @Transactional
    public void recordPlay(Long songId) {
        recordPlay(songId, null);
    }

    @Transactional
    public void recordPlay(Long songId, User fan) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalPlays(analytics.getTotalPlays() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);

        saveInteractionEvent(analytics.getSong(), fan, InteractionEvent.InteractionType.PLAY);
    }

    @Transactional
    public void recordLike(Long songId) {
        recordLike(songId, null);
    }

    @Transactional
    public void recordLike(Long songId, User fan) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalLikes(analytics.getTotalLikes() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);

        saveInteractionEvent(analytics.getSong(), fan, InteractionEvent.InteractionType.LIKE);
    }

    @Transactional
    public void recordComment(Long songId) {
        recordComment(songId, null);
    }

    @Transactional
    public void recordComment(Long songId, User fan) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalComments(analytics.getTotalComments() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);

        saveInteractionEvent(analytics.getSong(), fan, InteractionEvent.InteractionType.COMMENT);
    }

    @Transactional
    public void recordSave(Long songId, User fan) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalSaves(analytics.getTotalSaves() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);

        saveInteractionEvent(analytics.getSong(), fan, InteractionEvent.InteractionType.SAVE);
    }

    @Transactional
    public void recordReplay(Long songId, User fan) {
        SongAnalytics analytics = getOrCreate(songId);
        analytics.setTotalPlays(analytics.getTotalPlays() + 1);
        analytics.setLastUpdated(LocalDateTime.now());
        recalculateEngagement(analytics);
        analyticsRepository.save(analytics);

        saveInteractionEvent(analytics.getSong(), fan, InteractionEvent.InteractionType.REPLAY);
    }

    public SongAnalytics getSongAnalytics(Long songId) {
        return analyticsRepository.findBySongId(songId)
                .orElseThrow(() -> new NotFoundException("Analytics not found"));
    }

    public List<SongAnalytics> getArtistAnalytics(Long artistId) {
        List<Song> songs = songRepository.findByArtistId(artistId);
        return songs.stream()
                .map(song -> analyticsRepository.findBySongId(song.getId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get())
                .toList();
    }

    public List<FanInsightResponse> getFanInsights(Long artistId, int limit) {
        List<InteractionEvent> events = interactionEventRepository.findBySongArtistId(artistId);
        Map<Long, FanInsightResponse> insightMap = new HashMap<>();

        for (InteractionEvent event : events) {
            if (event.getFan() == null) {
                continue;
            }

            FanInsightResponse current = insightMap.getOrDefault(
                    event.getFan().getId(),
                    new FanInsightResponse(event.getFan().getId(), event.getFan().getUsername(), 0L, 0L, 0L, 0L)
            );

            current.setTotalInteractions(current.getTotalInteractions() + 1);
            if (event.getType() == InteractionEvent.InteractionType.PLAY) {
                current.setPlayCount(current.getPlayCount() + 1);
            } else if (event.getType() == InteractionEvent.InteractionType.LIKE) {
                current.setLikeCount(current.getLikeCount() + 1);
            } else if (event.getType() == InteractionEvent.InteractionType.COMMENT) {
                current.setCommentCount(current.getCommentCount() + 1);
            }

            insightMap.put(event.getFan().getId(), current);
        }

        return insightMap.values().stream()
                .sorted(Comparator.comparingLong(FanInsightResponse::getTotalInteractions).reversed())
                .limit(Math.max(1, limit))
                .toList();
    }

    public List<TrendPointResponse> getGrowthTrend(Long artistId, int days) {
        LocalDate fromDate = LocalDate.now().minusDays(Math.max(1, days - 1L));
        LocalDateTime fromDateTime = fromDate.atStartOfDay();

        List<InteractionEvent> events = interactionEventRepository.findBySongArtistIdAndCreatedAtAfter(artistId, fromDateTime);
        Map<LocalDate, long[]> buckets = new HashMap<>();

        for (int i = 0; i < days; i++) {
            buckets.put(fromDate.plusDays(i), new long[]{0L, 0L, 0L});
        }

        for (InteractionEvent event : events) {
            LocalDate day = event.getCreatedAt().toLocalDate();
            long[] metrics = buckets.get(day);
            if (metrics == null) {
                continue;
            }
            if (event.getType() == InteractionEvent.InteractionType.PLAY) {
                metrics[0]++;
            } else if (event.getType() == InteractionEvent.InteractionType.LIKE) {
                metrics[1]++;
            } else if (event.getType() == InteractionEvent.InteractionType.COMMENT) {
                metrics[2]++;
            }
        }

        List<TrendPointResponse> result = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate day = fromDate.plusDays(i);
            long[] metrics = buckets.get(day);
            double engagement = (metrics[0] + (3.0 * metrics[1]) + (5.0 * metrics[2])) / 100.0;
            result.add(new TrendPointResponse(day.toString(), metrics[0], metrics[1], metrics[2], engagement));
        }

        return result;
    }

    public List<Long> getReplayTimestamps(Long songId) {
        return interactionEventRepository
                .findBySongIdAndTypeOrderByCreatedAtAsc(songId, InteractionEvent.InteractionType.REPLAY)
                .stream()
                .map(event -> event.getCreatedAt().toEpochSecond(java.time.ZoneOffset.UTC))
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
                                .orElseThrow(() -> new NotFoundException("Song not found"));
                        SongAnalytics analytics = new SongAnalytics();
                        analytics.setSong(song);
                        return analyticsRepository.save(analytics);
                    } catch (org.springframework.dao.DataIntegrityViolationException e) {
                        // Prevent race condition duplicate creation
                        return analyticsRepository.findBySongId(songId)
                                .orElseThrow(() -> new ConflictException("Concurrent analytics write conflict"));
                    }
                });
    }

    private void saveInteractionEvent(Song song, User fan, InteractionEvent.InteractionType type) {
        InteractionEvent event = new InteractionEvent();
        event.setSong(song);
        event.setFan(fan);
        event.setType(type);
        interactionEventRepository.save(event);
    }
}