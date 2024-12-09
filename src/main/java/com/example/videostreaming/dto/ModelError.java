package com.example.videostreaming.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModelError {
    private int status;
    private String message;
}
