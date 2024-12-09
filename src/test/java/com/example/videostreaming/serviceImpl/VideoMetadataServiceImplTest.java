package com.example.videostreaming.serviceImpl;

import com.example.videostreaming.dto.VideoMetadataDTO;
import com.example.videostreaming.entity.Video;
import com.example.videostreaming.entity.VideoMetadata;
import com.example.videostreaming.exception.VideoInactiveException;
import com.example.videostreaming.exception.VideoNotFoundException;
import com.example.videostreaming.repositories.VideoMetadataRepository;
import com.example.videostreaming.repositories.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VideoMetadataServiceImplTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private VideoMetadataRepository videoMetadataRepository;

    @InjectMocks
    private VideoMetadataServiceImpl videoMetadataService;

    private Video video;
    public VideoMetadataDTO videoMetadataDTO;

    @BeforeEach
    void setUp() {
        video = new Video();
        video.setId(1L);
        video.setActive(true);
        video.setDeleted(false);

        videoMetadataDTO = new VideoMetadataDTO();
        videoMetadataDTO.setActor("Actor Name");
        videoMetadataDTO.setGenre("Action");
        videoMetadataDTO.setDirector("Director Name");
        videoMetadataDTO.setTitle("Video Title");
        videoMetadataDTO.setSynopsis("Video Synopsis");
        videoMetadataDTO.setRunningTime(120);
        videoMetadataDTO.setYearOfRelease(2024);
    }

    @Test
    void shouldUpdateVideoMetadataSuccessfully() throws VideoInactiveException {
        // Given
        VideoMetadata existingMetadata = new VideoMetadata();
        existingMetadata.setVideo(video);

        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoMetadataRepository.findByVideoId(1L)).thenReturn(Optional.of(existingMetadata));
        when(videoMetadataRepository.save(any(VideoMetadata.class))).thenReturn(existingMetadata);

        // When
        VideoMetadata updatedMetadata = videoMetadataService.updateVideoMetadata(1L, videoMetadataDTO);

        // Then
        assertNotNull(updatedMetadata);
        assertEquals("Actor Name", updatedMetadata.getActor());
        assertEquals("Action", updatedMetadata.getGenre());
        assertEquals("Director Name", updatedMetadata.getDirector());
        assertEquals("Video Title", updatedMetadata.getTitle());
        assertEquals("Video Synopsis", updatedMetadata.getSynopsis());
        assertEquals(120, updatedMetadata.getRunningTime());
        assertEquals(2024, updatedMetadata.getYearOfRelease());
        assertTrue(updatedMetadata.isActive());

        verify(videoRepository).findById(1L);
        verify(videoMetadataRepository).save(any(VideoMetadata.class));
    }

    @Test
    void shouldThrowVideoNotFoundExceptionWhenVideoNotFound() {
        // Given
        when(videoRepository.findById(1L)).thenReturn(Optional.empty());
        // When / Then
        VideoNotFoundException exception = assertThrows(VideoNotFoundException.class,
                () -> videoMetadataService.updateVideoMetadata(1L, videoMetadataDTO));
        assertEquals(VideoNotFoundException.class, exception.getClass());
    }

    @Test
    void shouldThrowVideoInactiveExceptionWhenVideoIsInactive() {
        // Given
        video.setActive(false);
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        // When / Then
        VideoInactiveException exception = assertThrows(VideoInactiveException.class,
                () -> videoMetadataService.updateVideoMetadata(1L, videoMetadataDTO));

        assertEquals(VideoInactiveException.class, exception.getClass());
    }

    @Test
    void shouldThrowVideoInactiveExceptionWhenVideoIsDeleted() {
        // Given
        video.setActive(false);
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));

        // When / Then
        VideoInactiveException exception = assertThrows(VideoInactiveException.class,
                () -> videoMetadataService.updateVideoMetadata(1L, videoMetadataDTO));

        assertEquals(VideoInactiveException.class, exception.getClass());
    }

    @Test
    void shouldCreateNewVideoMetadataWhenNotFound() throws VideoInactiveException {
        // Given
        video.setId(1L);
        video.setActive(true);

        VideoMetadataDTO videoMetadataDTO = new VideoMetadataDTO(
                "Actor Name", "Genre", "Director", "Title",
                2024, "Synopsis", 120, true);

        VideoMetadata savedMetadata = new VideoMetadata();
        savedMetadata.setActor("Actor Name");
        savedMetadata.setGenre("Genre");
        savedMetadata.setDirector("Director");
        savedMetadata.setTitle("Title");
        savedMetadata.setSynopsis("Synopsis");
        savedMetadata.setRunningTime(120);
        savedMetadata.setYearOfRelease(2024);
        savedMetadata.setActive(true);
        savedMetadata.setVideo(video);

        // When
        when(videoRepository.findById(1L)).thenReturn(Optional.of(video));
        when(videoMetadataRepository.findByVideoId(1L)).thenReturn(Optional.empty());
        when(videoMetadataRepository.save(any(VideoMetadata.class))).thenReturn(savedMetadata);

        VideoMetadata newMetadata = videoMetadataService.updateVideoMetadata(1L, videoMetadataDTO);

        // Then
        assertNotNull(newMetadata);
        assertEquals("Actor Name", newMetadata.getActor());
        assertEquals("Genre", newMetadata.getGenre());
        verify(videoMetadataRepository).save(any(VideoMetadata.class));
    }

}
