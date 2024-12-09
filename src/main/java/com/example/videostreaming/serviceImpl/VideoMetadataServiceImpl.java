package com.example.videostreaming.serviceImpl;

import com.example.videostreaming.dto.VideoMetadataDTO;
import com.example.videostreaming.entity.Video;
import com.example.videostreaming.entity.VideoMetadata;
import com.example.videostreaming.exception.VideoInactiveException;
import com.example.videostreaming.exception.VideoNotFoundException;
import com.example.videostreaming.repositories.VideoMetadataRepository;
import com.example.videostreaming.repositories.VideoRepository;
import com.example.videostreaming.services.VideoMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j
@Service
public class VideoMetadataServiceImpl implements VideoMetadataService {

    private final VideoRepository videoRepository;
    private final VideoMetadataRepository videoMetadataRepository;

    @Autowired
    public VideoMetadataServiceImpl(VideoRepository videoRepository, VideoMetadataRepository videoMetadataRepository) {
        this.videoRepository = videoRepository;
        this.videoMetadataRepository = videoMetadataRepository;
    }

    @Override
    @Transactional
    public VideoMetadata updateVideoMetadata(Long videoId, VideoMetadataDTO videoMetaDataDTO) throws VideoInactiveException {
        log.info("Updating metadata for video with ID: {}", videoId);
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> {
                    log.error("Video with ID {} not found", videoId);
                    return new VideoNotFoundException("Video does not exist");
                });

        if (!video.isActive() || video.isDeleted()) {
            throw new VideoInactiveException("Video not active or deleted");
        }

        VideoMetadata videoMetadata = videoMetadataRepository.findByVideoId(videoId)
                .orElse(new VideoMetadata());

        videoMetadata.setVideo(video);
        videoMetadata.setActor(videoMetaDataDTO.getActor());
        videoMetadata.setGenre(videoMetaDataDTO.getGenre());
        videoMetadata.setDirector(videoMetaDataDTO.getDirector());
        videoMetadata.setTitle(videoMetaDataDTO.getTitle());
        videoMetadata.setSynopsis(videoMetaDataDTO.getSynopsis());
        videoMetadata.setRunningTime(videoMetaDataDTO.getRunningTime());
        videoMetadata.setYearOfRelease(videoMetaDataDTO.getYearOfRelease());
        videoMetadata.setActive(true);

        VideoMetadata updatedMetadata = videoMetadataRepository.save(videoMetadata);
        log.info("Video metadata updated successfully for video ID: {}", videoId);

        return updatedMetadata;
    }
}
