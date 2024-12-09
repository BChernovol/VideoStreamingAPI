package com.example.videostreaming.repositories;

import com.example.videostreaming.entity.VideoMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoMetadataRepository extends JpaRepository<VideoMetadata,Long> {
    Optional<VideoMetadata> findByVideoId(Long videoId);
    Optional<List<VideoMetadata>> findVideoMetadataByDirector(String parameter);
}
