package com.example.videostreaming.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Blob;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "videos")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private boolean isDeleted;
    private boolean isActive;
    @Lob
    @Column(name = "video_data")
    @JsonIgnore
    private Blob videoData;
    @OneToOne(mappedBy = "video", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private VideoMetadata videoMetadata;

}
