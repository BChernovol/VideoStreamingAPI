package com.example.videostreaming.controllers;

import com.example.videostreaming.dto.VideoMetadataDTO;
import com.example.videostreaming.entity.Video;
import com.example.videostreaming.entity.VideoMetadata;
import com.example.videostreaming.exception.VideoInactiveException;
import com.example.videostreaming.services.VideoMetadataService;
import com.example.videostreaming.services.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@ControllerAdvice
@RequestMapping("/video")
public class VideoController {
    private final VideoService videoService;
    private final VideoMetadataService videoMetadataService;

    @Autowired
    public VideoController(VideoService videoService, VideoMetadataService videoMetadataService) {
        this.videoService = videoService;
        this.videoMetadataService = videoMetadataService;
    }

    @PostMapping("/publish")
    public Video publishVideo(@RequestParam("file") MultipartFile multipartFile) {
        return videoService.publishVideo(multipartFile);
    }

    @PutMapping("updateMetadata/{id}")
    public ResponseEntity<VideoMetadata> updateMetadata(@PathVariable Long id, @RequestBody VideoMetadataDTO metadataDTO) throws VideoInactiveException {
        VideoMetadata updatedVideo = videoMetadataService.updateVideoMetadata(id, metadataDTO);
        return ResponseEntity.ok(updatedVideo);
    }

    @DeleteMapping("softDeleteVideo/{id}")
    public void deleteVideo(@PathVariable Long id) {
        videoService.deleteVideoWithRelatedMetadata(id);
    }

    @GetMapping("loadVideo/{id}")
    public Video loadVideo(@PathVariable Long id) {
        return videoService.loadVideoById(id);
    }

    @GetMapping("/playVideo/{id}")
    public ResponseEntity<InputStreamResource> getVideo(@PathVariable Long id) {
        InputStream videoStream = videoService.playVideoById(id);

        return ResponseEntity.ok()
                .header("Content-Type", "video/mp4")
                .header("Content-Disposition", "inline; filename=\"video.mp4\"")
                .body(new InputStreamResource(videoStream));
    }

    @GetMapping("/getAllAvailableVideos")
    public ResponseEntity<List<Video>> getAllAvailableVideos() {
        List<Video> videos = videoService.getAllAvailableVideos();
        return ResponseEntity.status(HttpStatus.OK).body(videos);
    }

    @GetMapping("/getVideoByDirector/{parameter}")
    public ResponseEntity<List<Video>> getAllVideoByDirectorParam(@PathVariable String parameter) {
        List<Video> videos = videoService.getAllVideosByDirector(parameter);
        return ResponseEntity.status(HttpStatus.OK).body(videos);
    }
}
