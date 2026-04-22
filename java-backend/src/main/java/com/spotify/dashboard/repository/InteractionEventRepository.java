package com.spotify.dashboard.repository;

import com.spotify.dashboard.model.InteractionEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InteractionEventRepository extends JpaRepository<InteractionEvent, Long> {
    List<InteractionEvent> findBySongIdInAndCreatedAtAfter(List<Long> songIds, LocalDateTime from);

    List<InteractionEvent> findBySongArtistIdAndCreatedAtAfter(Long artistId, LocalDateTime from);

    List<InteractionEvent> findBySongArtistId(Long artistId);

    List<InteractionEvent> findBySongIdAndTypeOrderByCreatedAtAsc(Long songId, InteractionEvent.InteractionType type);
}
