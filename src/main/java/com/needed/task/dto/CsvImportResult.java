package com.needed.task.dto;

import java.util.List;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CsvImportResult {
    private int successCount;
    private int failedCount;
    private List<String> errors;

    public boolean hasErrors()
    {
        return !errors.isEmpty();
    }
    public CsvImportResult(int successCount, int failedCount, List<String> errors)
    {
        this.errors=errors;
        this.failedCount=failedCount;
        this.successCount=successCount;
    }
}
