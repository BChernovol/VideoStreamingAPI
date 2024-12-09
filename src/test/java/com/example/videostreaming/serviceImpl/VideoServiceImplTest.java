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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoServiceImplTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoMetadataRepository videoMetadataRepository;

    @Mock
    private VideoEngagementRepository videoEngagementRepository;

    @InjectMocks
    private VideoServiceImpl videoService;

    @Test
    void shouldPublishVideoSuccessfully() throws IOException, SQLException, IOException {
        // Given
        MultipartFile multipartFile = mock(MultipartFile.class);
        byte[] fileBytes = "video-content".getBytes();
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(fileBytes));

        // When
        Video video = Video.builder().videoData(new SerialBlob(fileBytes)).isActive(true).isDeleted(false).build();
        when(videoRepository.save(any(Video.class))).thenReturn(video);

        Video result = videoService.publishVideo(multipartFile);

        // Then
        assertNotNull(result);
        assertTrue(result.isActive());
        assertFalse(result.isDeleted());
        verify(videoRepository).save(any(Video.class));
    }

    @Test
    void shouldThrowExceptionWhenPublishingEmptyVideo() {
        // Given
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.isEmpty()).thenReturn(true);

        // When / Then
        assertThrows(EmptyVideoContentException.class, () -> videoService.publishVideo(multipartFile));
    }

    @Test
    void shouldDeleteVideoWithRelatedMetadata() {
        // Given
        Video video = new Video();
        video.setId(1L);
        video.setDeleted(false);
        video.setActive(true);

        VideoMetadata metadata = new VideoMetadata();
        metadata.setActive(true);

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoMetadataRepository.findByVideoId(1L)).thenReturn(Optional.of(metadata));

        // When
        videoService.deleteVideoWithRelatedMetadata(1L);

        // Then
        assertFalse(video.isActive());
        assertTrue(video.isDeleted());
        assertFalse(metadata.isActive());
        verify(videoRepository).save(video);
        verify(videoMetadataRepository).save(metadata);
    }

    @Test
    void shouldThrowVideoNotFoundExceptionWhenDeletingNonexistentVideo() {
        // Given
        when(videoRepository.findById(1L)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(VideoNotFoundException.class, () -> videoService.deleteVideoWithRelatedMetadata(1L));
    }

    @Test
    void shouldLoadVideoByIdAndUpdateImpressions() {
        // Given
        Video video = new Video();
        video.setId(1L);

        VideoEngagement engagement = new VideoEngagement();
        engagement.setImpressions(5);

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoEngagementRepository.findByVideoId(1L)).thenReturn(engagement);

        // When
        Video result = videoService.loadVideoById(1L);

        // Then
        assertNotNull(result);
        assertEquals(6, engagement.getImpressions());
        verify(videoEngagementRepository).findByVideoId(1L);
    }

    @Test
    void shouldThrowVideoIsDeletedExceptionWhenPlayingDeletedVideo() {
        // Given
        Video video = new Video();
        video.setId(1L);
        video.setDeleted(true);

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        // When / Then
        assertThrows(VideoIsDeleteException.class, () -> videoService.playVideoById(1L));
    }

    @Test
    void shouldPlayVideoAndUpdateViews() throws SQLException {
        // Given
        byte[] content = "video-content".getBytes();
        Blob blob = new SerialBlob(content);

        Video video = new Video();
        video.setId(1L);
        video.setDeleted(false);
        video.setVideoData(blob);

        VideoEngagement engagement = new VideoEngagement();
        engagement.setViews(10);

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoEngagementRepository.findByVideoId(1L)).thenReturn(engagement);

        // When
        InputStream result = videoService.playVideoById(1L);

        // Then
        assertNotNull(result);
        assertEquals(11, engagement.getViews());
    }

    @Test
    void shouldGetAllAvailableVideos() {
        // Given
        Video video1 = new Video();
        Video video2 = new Video();
        List<Video> videos = List.of(video1, video2);

        when(videoRepository.findAllVideoByIsDeletedFalse()).thenReturn(videos);

        // When
        List<Video> result = videoService.getAllAvailableVideos();

        // Then
        assertEquals(2, result.size());
        verify(videoRepository).findAllVideoByIsDeletedFalse();
    }

    @Test
    void shouldGetAllVideosByDirector() {
        // Given
        Video video1 = new Video();
        video1.setId(1L);

        Video video2 = new Video();
        video2.setId(2L);

        VideoMetadata metadata1 = new VideoMetadata();
        metadata1.setVideo(video1);

        VideoMetadata metadata2 = new VideoMetadata();
        metadata2.setVideo(video2);

        when(videoMetadataRepository.findVideoMetadataByDirector("Spielberg"))
                .thenReturn(Optional.of(List.of(metadata1, metadata2)));
        when(videoRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(video1, video2));

        // When
        List<Video> result = videoService.getAllVideosByDirector("Spielberg");

        // Then
        assertEquals(2, result.size());
        verify(videoRepository).findAllById(List.of(1L, 2L));
    }
}

