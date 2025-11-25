package com.needed.task.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;




@Service
public class FileService {
    @Value("${upload.path:uploads}")
    private String uploadDir;
    public String StoreFile (MultipartFile file)  throws IOException
        {
             // Проверка пустого файла
        if (file.isEmpty()) {
            throw new IOException("Файл пустой");
        }
        Path uploadPath= Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        String filename=UUID.randomUUID().toString();
        Path target=uploadPath.resolve(filename);
        Files.copy((file.getInputStream()),target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
        }

    public boolean deleteFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
} 

