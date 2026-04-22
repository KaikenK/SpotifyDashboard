package com.spotify.dashboard.service;

import com.spotify.dashboard.dto.AlbumAnalyticsResponse;
import com.spotify.dashboard.exception.BadRequestException;
import com.spotify.dashboard.exception.NotFoundException;
import com.spotify.dashboard.model.Album;
import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.SongAnalytics;
import com.spotify.dashboard.model.User;
import com.spotify.dashboard.repository.AlbumRepository;
import com.spotify.dashboard.repository.SongAnalyticsRepository;
import com.spotify.dashboard.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;
    private final SongAnalyticsRepository analyticsRepository;

    public Album createAlbum(User artist, String title, String coverArt, java.time.LocalDate releaseDate) {
        if (title == null || title.isBlank()) {
            throw new BadRequestException("Album title is required");
        }
        Album album = new Album();
        album.setArtist(artist);
        album.setTitle(title.trim());
        album.setCoverArt(coverArt);
        album.setReleaseDate(releaseDate);
        return albumRepository.save(album);
    }

    public Album updateAlbum(User artist, Long albumId, String title, String coverArt, java.time.LocalDate releaseDate) {
        Album album = getOwnedAlbum(artist, albumId);
        if (title != null && !title.isBlank()) {
            album.setTitle(title.trim());
        }
        album.setCoverArt(coverArt);
        album.setReleaseDate(releaseDate);
        return albumRepository.save(album);
    }

    public Song addSongToAlbum(User artist, Long albumId, Long songId) {
        Album album = getOwnedAlbum(artist, albumId);
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new NotFoundException("Song not found"));
        if (!song.getArtist().getId().equals(artist.getId())) {
            throw new BadRequestException("You can only manage your own songs");
        }
        song.setAlbum(album);
        return songRepository.save(song);
    }

    public List<Album> getArtistAlbums(User artist) {
        return albumRepository.findByArtistId(artist.getId());
    }

    public AlbumAnalyticsResponse getAlbumAnalytics(User artist, Long albumId) {
        Album album = getOwnedAlbum(artist, albumId);
        List<Song> songs = songRepository.findByAlbumId(album.getId());
        List<SongAnalytics> allAnalytics = songs.stream()
                .map(song -> analyticsRepository.findBySongId(song.getId()))
                .filter(java.util.Optional::isPresent)
                .map(java.util.Optional::get)
                .toList();

        long totalPlays = allAnalytics.stream().mapToLong(SongAnalytics::getTotalPlays).sum();
        long totalLikes = allAnalytics.stream().mapToLong(SongAnalytics::getTotalLikes).sum();
        long totalComments = allAnalytics.stream().mapToLong(SongAnalytics::getTotalComments).sum();
        long totalSaves = allAnalytics.stream().mapToLong(SongAnalytics::getTotalSaves).sum();
        double avgEngagement = allAnalytics.isEmpty()
                ? 0.0
                : allAnalytics.stream().mapToDouble(SongAnalytics::getEngagementScore).average().orElse(0.0);

        return new AlbumAnalyticsResponse(
                album.getId(),
                album.getTitle(),
                (long) songs.size(),
                totalPlays,
                totalLikes,
                totalComments,
                totalSaves,
                avgEngagement
        );
    }

    private Album getOwnedAlbum(User artist, Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new NotFoundException("Album not found"));
        if (!album.getArtist().getId().equals(artist.getId())) {
            throw new BadRequestException("You can only manage your own albums");
        }
        return album;
    }
}
