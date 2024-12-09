package com.example.videostreaming.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "videos_meta_data")
public class VideoMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String title;
    private String synopsis;
    private String director;
    private String actor;
    private int yearOfRelease;
    private String genre;
    private int runningTime;
    private boolean isActive;
    @OneToOne
    @JoinColumn(name = "video_id", referencedColumnName = "id")
    @JsonBackReference
    private Video video;
}
