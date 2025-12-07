package com.needed.task.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.needed.task.model.Alert;
import com.needed.task.service.AlertService;
import com.needed.task.service.FileService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api") // ← вынесено сюда для единообразия
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final AlertService alertService; 

    @PostMapping("/alerts/{id}/upload/")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity <String> uploadFile(
        @RequestParam ("file") MultipartFile file,
        @PathVariable Long id)
    { 
        // Проверяем, существует ли инцидент
        Optional<Alert> alertOpt = alertService.findById(id);
        if (alertOpt.isEmpty()) {
            return ResponseEntity.notFound().build(); // <-- 404 лучше, чем 400
        }
        try {
         //Сохраняем файл
            String filePath = fileService.StoreFile(file); // ← storeFile (lowercase)

            //Привязываем к инциденту
            Alert updatedAlert = alertService.addFileToAlert(id, filePath);
            return ResponseEntity.ok(filePath);
    }
    catch (IOException e)
        {
        log.error("I/O error uploading file for alert ID={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка сохранения файла: " + e.getMessage());
        }
    catch (Exception e) {
            log.error("Unexpected error uploading file for alert ID={}", id, e);
            return ResponseEntity.badRequest()
                    .body("Ошибка обработки: " + e.getMessage());
        }
    }

}
