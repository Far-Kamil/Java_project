package com.needed.task.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.needed.task.dto.CsvImportResult;
import com.needed.task.enums.EventType;
import com.needed.task.enums.StatusType;
import com.needed.task.model.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvImportService {
    private final AlertService alertService;
    private CSVFormat createCsvFormat()
    {
        return CSVFormat.DEFAULT.builder()
        .setHeader() // ожидаем заголовки (автоопределение)
        .setSkipHeaderRecord(true) // пропускаем первую строку как заголовок
        .setIgnoreHeaderCase(true) // игнорируем регистр заголовков: "Price" == "price"
        .setTrim(true)  // убираем пробелы по краям
        .build();
    }
    public CsvImportResult importAlertFromCsv(MultipartFile file)
    {
         if (file == null || file.isEmpty()) {
        return new CsvImportResult(0, 0, 
            Collections.singletonList("File is empty or null"));
    }

        List<Alert> valid = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        try (
            BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream(),
            StandardCharsets.UTF_8));
            CSVParser csvParser= new CSVParser(reader, createCsvFormat()))
            {
               for (CSVRecord csvRecord: csvParser) {
                try {
                     processCsv(csvRecord, valid, errors);
                } catch (Exception e) {
                     // Не прерываем импорт из-за одной строки -> логируем и продолжаем
                     String msg = String.format(
                            "Line %d: %s (record: %s)",
                            csvRecord.getRecordNumber(),
                            e.getMessage() != null ? e.getMessage() : "Unknown error",
                            csvRecord.toString()
                     );
                    errors.add(msg);
                    log.warn("Failed to parse CSV record", e);
                }
           
            }
            List <Alert> success= saveValid(valid);
            return new CsvImportResult(
                success.size(),
                valid.size()-success.size(),
                errors
            );
        }
        catch (IOException e) {
           log.error("Error while reading", e);
           errors.add("Did not read: "+ e.getMessage());
           return new CsvImportResult(0, 0, null);
        }   
    }
    private List<Alert> saveValid(List<Alert> valid){
        List <Alert> success = new ArrayList<>();
        for(Alert alert : valid){
        try {
             // alertService.create() возвращает Alert — сохраняем его (вдруг id проставлен)
            Alert saved = alertService.create(alert);
            success.add(saved);
        } 
        catch (Exception e) {
            log.error("Failed to save alert: [type={}, location={}, timestamp={}]", 
                alert.getType(), alert.getLocation(), alert.getTimestamp(), e);
        }
    }
    return success;
    }
    // Обработка одной строки CSV
    private void processCsv(CSVRecord csvRecord,
        List<Alert> valid, List<String> errors)
    {
        try {
        // 1. Тип инцидента (обязательный)
        String typeStr = csvRecord.get("type").trim();
        if (typeStr.isEmpty()) {
            throw new IllegalArgumentException("Missing 'type'");
        }
        EventType type = EventType.valueOf(typeStr.toUpperCase());

        // 2. Время (обязательное)
        String timestampStr = csvRecord.get("timestamp").trim();
        if (timestampStr.isEmpty()) {
            throw new IllegalArgumentException("Missing 'timestamp'");
        }
        LocalDateTime timestamp = LocalDateTime.parse(timestampStr);

        // 3. Локация и описание (обязательные)
        String location = csvRecord.get("location").trim();
        if (location.isEmpty()) {
            throw new IllegalArgumentException("Missing 'location'");
        }
        String description = csvRecord.get("description").trim();
        if (description.isEmpty()) {
            throw new IllegalArgumentException("Missing 'description'");
        }

        // 4. Статус (необязательный, по умолчанию NEW)
        StatusType status = StatusType.NEW;
        String statusStr = csvRecord.get("status");
        if (statusStr != null && !statusStr.trim().isEmpty()) {
            status = StatusType.valueOf(statusStr.trim().toUpperCase());
        }

        // Создаём Alert
        Alert alert = new Alert();
        alert.setType(type);
        alert.setTimestamp(timestamp);
        alert.setLocation(location);
        alert.setDescription(description);
        alert.setStatus(status);

        valid.add(alert);
        } 
       catch (IllegalArgumentException | DateTimeParseException | NullPointerException e) {
            throw new IllegalArgumentException("Parsing error: " + e.getMessage(), e);
        }   
    }
}

