package com.example.videostreaming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
