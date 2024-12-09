package com.example.videostreaming.conrtollers;

import com.example.videostreaming.dto.ModelError;
import com.example.videostreaming.exception.EmptyVideoContentException;
import com.example.videostreaming.exception.VideoInactiveException;
import com.example.videostreaming.exception.VideoIsDeleteException;
import com.example.videostreaming.exception.VideoNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class VideoExceptionHandlerControllerAdvice {

    @ExceptionHandler(VideoIsDeleteException.class)
    public ResponseEntity<ModelError> handleVideoIsDeleteException(VideoIsDeleteException e) {
        log.info("Video was removing, playing not possible");
        return ResponseEntity.status(705)
                .body(new ModelError(705, "Video was removing, playing not possible"));
    }

    @ExceptionHandler(EmptyVideoContentException.class)
    public ResponseEntity<ModelError> handleEmptyVideoContentException(EmptyVideoContentException e) {
        log.info("Video content is null or empty");
        return ResponseEntity.status(706)
                .body(new ModelError(706, "Video content is null or empty"));
    }

    @ExceptionHandler(VideoNotFoundException.class)
    public ResponseEntity<ModelError> handleVideoNotFoundException(VideoNotFoundException e) {
        log.info("Video not found.");
        return ResponseEntity.status(707)
                .body(new ModelError(707, "Video not found."));
    }

    @ExceptionHandler(VideoInactiveException.class)
    public ResponseEntity<ModelError> handleVideoInactiveException(VideoInactiveException e) {
        log.info("Video inactive or deleted");
        return ResponseEntity.status(708)
                .body(new ModelError(708, "Video inactive or deleted"));
    }

    @ExceptionHandler(NestedRuntimeException.class)
    public ResponseEntity<ModelError> handleVideoMultipartFileIsNotValid(NestedRuntimeException e) {
        log.info("Video file is not valid.");
        return ResponseEntity.status(709)
                .body(new ModelError(709, "Video file is not valid."));
    }


}

