package com.needed.task.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    @Value("${upload.path:uploads}")
    private String uploadDir;

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "png", "jpg", "jpeg", "gif", "pdf", "txt"
    );

    public String StoreFile (MultipartFile file)  throws IOException
    {
        // Проверка пустого файла
        if (file.isEmpty()) {
            throw new IOException("Файл пустой");
        }
        // Безопасное имя (убираем ../, \, и т.д.)
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Недопустимое имя файла: " + originalFilename);
        }
        // Проверка расширения
        String extension = getFileExtension(originalFilename);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Недопустимый тип файла. Разрешены: " + ALLOWED_EXTENSIONS);
        }
         //Создаём директорию
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

       // 5. Генерируем уникальное имя, сохраняем РАСШИРЕНИЕ
        String filename = UUID.randomUUID() + "." + extension;
        Path targetPath = uploadPath.resolve(filename);

        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
        }

    public boolean deleteFile(String fileName) {
        try {
             if (fileName == null || fileName.contains("..") || fileName.isEmpty()) {
                return false;
            }
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", fileName, e);
            return false;
        }
    }
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
    }
} 

