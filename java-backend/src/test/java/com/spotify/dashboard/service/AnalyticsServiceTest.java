package com.spotify.dashboard.service;

import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.SongAnalytics;
import com.spotify.dashboard.repository.InteractionEventRepository;
import com.spotify.dashboard.repository.SongAnalyticsRepository;
import com.spotify.dashboard.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private SongAnalyticsRepository analyticsRepository;
    @Mock
    private SongRepository songRepository;
    @Mock
    private InteractionEventRepository interactionEventRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void recordPlayIncrementsPlayCountAndRecalculatesScore() {
        Song song = new Song();
        song.setId(3L);

        SongAnalytics analytics = new SongAnalytics();
        analytics.setSong(song);
        analytics.setTotalPlays(10L);
        analytics.setTotalLikes(2L);
        analytics.setTotalComments(1L);
        analytics.setTotalSaves(0L);

        when(analyticsRepository.findBySongId(3L)).thenReturn(Optional.of(analytics));

        analyticsService.recordPlay(3L);

        assertEquals(11L, analytics.getTotalPlays());
        assertTrue(analytics.getEngagementScore() > 0.0);
        verify(analyticsRepository).save(analytics);
    }

    @Test
    void getSongAnalyticsThrowsWhenMissing() {
        when(analyticsRepository.findBySongId(55L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> analyticsService.getSongAnalytics(55L));
    }
}