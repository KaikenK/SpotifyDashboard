package com.spotify.dashboard;

import com.spotify.dashboard.model.*;
import com.spotify.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final SongAnalyticsRepository analyticsRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedAdmin();
        seedArtist();
        seedFan();
        System.out.println("=== Seed data loaded ===");
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail("admin@spotify.com")) return;
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@spotify.com");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(User.Role.ADMIN);
        admin.setIsVerified(true);
        userRepository.save(admin);
    }

    private void seedArtist() {
        if (userRepository.existsByEmail("artist@spotify.com")) return;
        User artist = new User();
        artist.setUsername("DemoArtist");
        artist.setEmail("artist@spotify.com");
        artist.setPasswordHash(passwordEncoder.encode("artist123"));
        artist.setRole(User.Role.ARTIST);
        artist.setIsVerified(true);
        userRepository.save(artist);

        String[] covers = {
                "https://images.unsplash.com/photo-1587659927818-f89edd4db555?crop=entropy&cs=srgb&fm=jpg&w=400",
                "https://images.pexels.com/photos/13069544/pexels-photo-13069544.png?auto=compress&cs=tinysrgb&w=400",
                "https://images.unsplash.com/photo-1642444525640-d3eb287ed389?crop=entropy&cs=srgb&fm=jpg&w=400"
        };

        createSong(artist, "Midnight Echoes", 234, Song.SongStatus.PUBLISHED, covers[0], 42300, 3200, 280, 1500);
        createSong(artist, "Neon Dreams", 198, Song.SongStatus.PUBLISHED, covers[1], 31143, 2800, 190, 980);
        createSong(artist, "Gravity Pull", 267, Song.SongStatus.PUBLISHED, covers[2], 18500, 1400, 95, 620);
        createSong(artist, "Electric Soul", 312, Song.SongStatus.PENDING_APPROVAL, covers[0], 0, 0, 0, 0);
        createSong(artist, "Fading Light", 180, Song.SongStatus.UPLOADED, covers[1], 0, 0, 0, 0);
    }

    private void createSong(User artist, String title, int duration, Song.SongStatus status,
                            String cover, long plays, long likes, long comments, long saves) {
        Song song = new Song();
        song.setArtist(artist);
        song.setTitle(title);
        song.setDurationSec(duration);
        song.setCoverArt(cover);
        song.setStatus(status);
        if (status == Song.SongStatus.PUBLISHED) {
            song.setPublishedAt(LocalDateTime.now());
        }
        songRepository.save(song);

        if (status == Song.SongStatus.PUBLISHED) {
            SongAnalytics analytics = new SongAnalytics();
            analytics.setSong(song);
            analytics.setTotalPlays(plays);
            analytics.setTotalLikes(likes);
            analytics.setTotalComments(comments);
            analytics.setTotalSaves(saves);
            double score = Math.min(((plays * 1.0) + (likes * 3.0) + (comments * 5.0) + (saves * 4.0)) / 100.0, 100.0);
            analytics.setEngagementScore(score);
            analyticsRepository.save(analytics);
        }
    }

    private void seedFan() {
        if (userRepository.existsByEmail("fan@spotify.com")) return;
        User fan = new User();
        fan.setUsername("MusicFan");
        fan.setEmail("fan@spotify.com");
        fan.setPasswordHash(passwordEncoder.encode("fan123"));
        fan.setRole(User.Role.FAN);
        fan.setIsVerified(true);
        userRepository.save(fan);
    }
}
