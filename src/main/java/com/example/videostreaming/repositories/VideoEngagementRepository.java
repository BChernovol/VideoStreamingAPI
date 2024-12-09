package com.example.videostreaming.repositories;

import com.example.videostreaming.entity.VideoEngagement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoEngagementRepository extends JpaRepository<VideoEngagement, Long> {
    VideoEngagement findByVideoId(Long id);
}
