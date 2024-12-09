package com.example.videostreaming.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class VideoDTO {
    private MultipartFile multipartFile;
}
