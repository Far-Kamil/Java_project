package com.needed.task.controller;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.needed.task.enums.StatusType;
import com.needed.task.model.Alert;
import com.needed.task.service.CachedAlertService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/alerts")
public class MainController {
    private final CachedAlertService alertService;
    public MainController (CachedAlertService alertService)
    {
        this.alertService= alertService;
    }

    @GetMapping
    private List<Alert> getAllAlerts(@RequestParam(required = false) StatusType status)
    {
        if (status != null) 
        {
            return alertService.findByStatus(status);
        }
        return alertService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getById(@PathVariable Long id) 
    {
        return alertService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bus/{busId}")
    public List<Alert> getAlertsByBus(@PathVariable Long busId) 
    {
        return alertService.findByBusId(busId);
    }

    @PostMapping
    public ResponseEntity<?> createAlert(@RequestBody @Valid Alert alert, BindingResult result) 
    {
        if(result.hasErrors())
        {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        Alert createdAlert= alertService.create(alert);
        return ResponseEntity.created(URI.create("/api/alerts/" + createdAlert.getId()))
        .body(createdAlert);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity <Alert> updateStatus(@PathVariable Long id, 
    @RequestParam StatusType status)
    {
        try 
        {
            Alert updatedAlert = alertService.updateStatus(id, status);
            return ResponseEntity.ok(updatedAlert);
        } 
        catch (RuntimeException e) 
        {
            return ResponseEntity.notFound().build();
        }  
    }    
    @DeleteMapping("/{id}")
    public ResponseEntity <Void> deleteAlert(@PathVariable Long id)
    {
        try 
        {
            alertService.deleteById(id);
            return ResponseEntity.noContent().build();
        } 
        catch (RuntimeException e) 
        {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Endpoint for cache management (for administration)
    @PostMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        alertService.clearAllCache();
        return ResponseEntity.ok("Кеш успешно очищен");
    }
}
