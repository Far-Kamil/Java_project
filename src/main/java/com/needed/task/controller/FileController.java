package com.needed.task.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.needed.task.model.Alert;
import com.needed.task.service.AlertService;
import com.needed.task.service.CachedAlertService;
import com.needed.task.service.FileService;

@RestController
public class FileController {
    private final FileService fileService;
    private final AlertService alertService; 
    public FileController (FileService fileService, AlertService alertService)
    {
        this.fileService= fileService;
        this.alertService=alertService;
    }
    @PostMapping(value = "api/alerts/{id}/upload/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, 
    produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity <String> uploadFile(
        @RequestParam ("file") MultipartFile file,
        @PathVariable Long id
    )
    {
        try {
        if (!alertService.findById(id).isPresent()) {
            return ResponseEntity.badRequest().body("Инцидент не найден");}
        String resultFile=fileService.StoreFile(file);
        Alert updatedAlert= alertService.addFileToAlert(id, resultFile);
        return ResponseEntity.ok(resultFile);
    }
       
    catch (IOException e)
        {
        return ResponseEntity.badRequest()
        .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }

}
