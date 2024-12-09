package com.example.videostreaming.serviceImpl;

import com.example.videostreaming.entity.Video;
import com.example.videostreaming.entity.VideoEngagement;
import com.example.videostreaming.entity.VideoMetadata;
import com.example.videostreaming.exception.EmptyVideoContentException;
import com.example.videostreaming.exception.VideoIsDeleteException;
import com.example.videostreaming.exception.VideoNotFoundException;
import com.example.videostreaming.repositories.VideoEngagementRepository;
import com.example.videostreaming.repositories.VideoMetadataRepository;
import com.example.videostreaming.repositories.VideoRepository;
import com.example.videostreaming.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final VideoMetadataRepository videoMetadataRepository;
    private final VideoEngagementRepository videoEngagementRepository;

    @Autowired
    public VideoServiceImpl(VideoRepository videoRepository, VideoMetadataRepository videoMetadataRepository, VideoEngagementRepository videoEngagementRepository) {
        this.videoRepository = videoRepository;
        this.videoMetadataRepository = videoMetadataRepository;
        this.videoEngagementRepository = videoEngagementRepository;
    }

    @Override
    @Transactional
    public Video publishVideo(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty()) {
            throw new EmptyVideoContentException("Uploaded video file is empty or null");
        }

        try (var inputStream = multipartFile.getInputStream()) {
            byte[] fileBytes = inputStream.readAllBytes();
            Blob blob = new SerialBlob(fileBytes);
            Video video = Video.builder()
                    .videoData(blob)
                    .isActive(true)
                    .isDeleted(false)
                    .build();
            return videoRepository.save(video);

        } catch (IOException e) {
            throw new RuntimeException("Error reading video file content", e);
        } catch (SQLException e) {
            throw new RuntimeException("Error converting video content to Blob", e);
        }
    }

    @Override
    @Transactional
    public void deleteVideoWithRelatedMetadata(Long videoId) {
        Video video = findVideoById(videoId);
        video.setDeleted(true);
        video.setActive(false);

        VideoMetadata videoMetadata = findVideoMetadataByVideoId(videoId);
        if (videoMetadata != null) {
            videoMetadata.setActive(false);
            videoMetadataRepository.save(videoMetadata);
        }
        videoRepository.save(video);
    }

    @Override
    @Transactional
    public Video loadVideoById(Long videoId) {
        Video video = findVideoById(videoId);

        VideoEngagement videoEngagement = findVideoEngagementByVideoId(video);
        if (videoEngagement != null) {
            videoEngagement.setImpressions(videoEngagement.getImpressions() + 1);
        } else {
            VideoEngagement newVideoEngagement = VideoEngagement.builder().video(video)
                    .impressions(1)
                    .build();
            videoEngagementRepository.save(newVideoEngagement);
        }

        return video;
    }

    @Override
    @Transactional
    public InputStream playVideoById(Long id) {
        Video video = findVideoById(id);
        if (video.isDeleted()) {
            throw new VideoIsDeleteException();
        }
        VideoEngagement videoEngagement = findVideoEngagementByVideoId(video);
        if (videoEngagement != null) {
            videoEngagement.setViews(videoEngagement.getViews() + 1);
        } else {
            VideoEngagement newVideoEngagement = VideoEngagement.builder().video(video)
                    .views(1)
                    .build();
            videoEngagementRepository.save(newVideoEngagement);
        }
        try {
            Blob videoData = video.getVideoData();
            if (videoData == null) {
                throw new RuntimeException("Video data is empty");
            }
            byte[] videoBytes = videoData.getBytes(1, (int) videoData.length());
            return new ByteArrayInputStream(videoBytes);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read video data", e);
        }
    }

    @Override
    @Transactional
    public List<Video> getAllAvailableVideos() {
        return videoRepository.findAllVideoByIsDeletedFalse();
    }


    @Override
    @Transactional
    public List<Video> getAllVideosByDirector(String parameter) {
        List<Long> videoIds = videoMetadataRepository.findVideoMetadataByDirector(parameter)
                .map(metadata -> metadata.stream()
                        .filter(videoMetadata -> videoMetadata.getVideo() != null)
                        .map(videoMetadata -> videoMetadata.getVideo().getId())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        if (videoIds.isEmpty()) {
            return Collections.emptyList();
        }
        return videoRepository.findAllById(videoIds);
    }

    private Video findVideoById(Long videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new VideoNotFoundException("Not found video with ID: " + videoId));
    }
    private VideoMetadata findVideoMetadataByVideoId(Long videoId) {
        return videoMetadataRepository.findByVideoId(videoId).orElse(null);
    }
    private VideoEngagement findVideoEngagementByVideoId(Video video) {
        return videoEngagementRepository.findByVideoId(video.getId());
    }

}
