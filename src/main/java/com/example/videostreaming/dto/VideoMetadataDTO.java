package com.example.videostreaming.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoMetadataDTO {
    private String title;
    private String synopsis;
    private String director;
    private String actor;
    private int yearOfRelease;
    private String genre;
    private int runningTime;
    private boolean isActive;
}
