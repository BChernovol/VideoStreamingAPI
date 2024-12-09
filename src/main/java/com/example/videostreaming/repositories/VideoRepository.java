package com.example.videostreaming.repositories;

import com.example.videostreaming.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video,Long> {
    List<Video> findAllVideoByIsDeletedFalse();
}
