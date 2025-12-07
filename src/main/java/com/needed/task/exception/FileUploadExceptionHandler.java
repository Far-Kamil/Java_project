package com.needed.task.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class FileUploadExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSize(Exception e) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "File too large");
        body.put("message", "Maximum file size is 10 MB");
        return ResponseEntity.badRequest().body(body);
    }
}
