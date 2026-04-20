package com.spotify.dashboard.repository;

import com.spotify.dashboard.model.Song;
import com.spotify.dashboard.model.Song.SongStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findByArtistId(Long artistId);
    List<Song> findByStatus(SongStatus status);
    List<Song> findByArtistIdAndStatus(Long artistId, SongStatus status);
    List<Song> findByAlbumId(Long albumId);
}