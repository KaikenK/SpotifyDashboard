package com.spotify.dashboard.service;

import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.Follow;
import com.spotify.dashboard.model.Notification;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.SongAnalytics;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.FollowRepository;
import com.spotify.dashboard.repository.NotificationRepository;
import com.spotify.dashboard.repository.SongAnalyticsRepository;
import com.spotify.dashboard.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SongServiceTest {

    @Mock
    private SongRepository songRepository;
    @Mock
    private SongAnalyticsRepository analyticsRepository;
    @Mock
    private NotificationRepository notificationRepository;
    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private SongService songService;

    @Test
    void approveSongPublishesCreatesAnalyticsAndSendsNotifications() {
        User artist = new User();
        artist.setId(11L);
        artist.setUsername("DemoArtist");

        Song song = new Song();
        song.setId(99L);
        song.setTitle("Midnight Echoes");
        song.setArtist(artist);

        User fan = new User();
        fan.setId(42L);

        Follow follow = new Follow();
        follow.setFan(fan);
        follow.setArtist(artist);
        follow.setIsSubscribed(true);

        when(songRepository.findById(99L)).thenReturn(Optional.of(song));
        when(songRepository.save(any(Song.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(followRepository.findByArtistIdAndIsSubscribedTrue(11L)).thenReturn(List.of(follow));

        Song approved = songService.approveSong(99L);

        assertEquals(Song.SongStatus.PUBLISHED, approved.getStatus());
        assertNotNull(approved.getPublishedAt());
        verify(analyticsRepository).save(any(SongAnalytics.class));
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    void getSongByIdThrowsWhenSongMissing() {
        when(songRepository.findById(777L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> songService.getSongById(777L));
    }
}