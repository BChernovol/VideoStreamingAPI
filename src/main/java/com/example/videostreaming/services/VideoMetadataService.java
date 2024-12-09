package com.example.videostreaming.services;

import com.example.videostreaming.dto.VideoMetadataDTO;
import com.example.videostreaming.entity.VideoMetadata;
import com.example.videostreaming.exception.VideoInactiveException;

public interface VideoMetadataService {
    VideoMetadata updateVideoMetadata(Long id, VideoMetadataDTO videoMetaDataDTO) throws VideoInactiveException;
}
