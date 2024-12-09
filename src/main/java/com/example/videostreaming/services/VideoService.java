package com.example.videostreaming.services;

import com.example.videostreaming.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface VideoService {
    Video publishVideo(MultipartFile file);
    void deleteVideoWithRelatedMetadata(Long id);
    Video loadVideoById(Long id);
    InputStream playVideoById(Long id);
    List<Video> getAllAvailableVideos();
    List<Video> getAllVideosByDirector(String director);
}
