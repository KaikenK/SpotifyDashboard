package com.spotify.dashboard.service;

import com.spotify.dashboard.model.*;
import com.spotify.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SongService {

    private final SongRepository songRepository;
    private final SongAnalyticsRepository analyticsRepository;
    private final NotificationRepository notificationRepository;
    private final FollowRepository followRepository;

    public Song uploadSong(User artist, String title,
                           Integer durationSec, Album album) {
        Song song = new Song();
        song.setArtist(artist);
        song.setTitle(title);
        song.setDurationSec(durationSec);
        song.setAlbum(album);
        song.setStatus(Song.SongStatus.UPLOADED);
        return songRepository.save(song);
    }

    public Song submitForApproval(Long songId) {
        Song song = getSongById(songId);
        song.setStatus(Song.SongStatus.PENDING_APPROVAL);
        return songRepository.save(song);
    }

    @Transactional
    public Song approveSong(Long songId) {
        Song song = getSongById(songId);
        song.setStatus(Song.SongStatus.PUBLISHED);
        song.setPublishedAt(LocalDateTime.now());
        songRepository.save(song);

        // Create analytics record for this song
        SongAnalytics analytics = new SongAnalytics();
        analytics.setSong(song);
        analyticsRepository.save(analytics);

        // Notify all subscribed fans
        notifySubscribers(song);

        return song;
    }

    public Song rejectSong(Long songId) {
        Song song = getSongById(songId);
        song.setStatus(Song.SongStatus.REJECTED);
        return songRepository.save(song);
    }

    public Song unpublishSong(Long songId) {
        Song song = getSongById(songId);
        song.setStatus(Song.SongStatus.UNPUBLISHED);
        return songRepository.save(song);
    }

    public List<Song> getPublishedSongs() {
        return songRepository.findByStatus(Song.SongStatus.PUBLISHED);
    }

    public List<Song> getPendingSongs() {
        return songRepository.findByStatus(Song.SongStatus.PENDING_APPROVAL);
    }

    public List<Song> getArtistSongs(Long artistId) {
        return songRepository.findByArtistId(artistId);
    }

    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Song not found"));
    }

    private void notifySubscribers(Song song) {
        List<Follow> subscribers = followRepository
                .findByArtistIdAndIsSubscribedTrue(song.getArtist().getId());

        for (Follow follow : subscribers) {
            Notification notification = new Notification();
            notification.setRecipient(follow.getFan());
            notification.setArtist(song.getArtist());
            notification.setSong(song);
            notification.setMessage(song.getArtist().getUsername()
                    + " just released a new song: " + song.getTitle());
            notificationRepository.save(notification);
        }
    }
}
