package com.spotify.dashboard.repository;

import com.spotify.dashboard.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findBySongIdAndIsRemovedFalse(Long songId);
    List<Comment> findByFanId(Long fanId);
}