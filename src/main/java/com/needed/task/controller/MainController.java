package com.needed.task.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.needed.task.model.Alert;
import com.needed.task.service.AlertService;

import jakarta.validation.Valid;

@RestController
public class MainController {
    private final AlertService alertService;
    public MainController (AlertService alertService)
    {
        this.alertService= alertService;
    }
     @GetMapping("/alerts")
    private List<Alert> getAlerts(){
        return alertService.getAll();
    }
     @PostMapping("/alerts")
    public ResponseEntity<Alert> addAlert(@RequestBody @Valid Alert alert) {
        alertService.create(alert);
        return ResponseEntity.status(HttpStatus.CREATED).body(alert);
    }
    @GetMapping("/alerts/{id}")
    public ResponseEntity <Alert> getAlertById(@PathVariable Long id)
    {
        Alert alert = alertService.getById(id);
        if(alert!=null)
        {
            return ResponseEntity.ok(alert);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);   
    }    
    @DeleteMapping("/alerts/{id}")
    public ResponseEntity <Void> delete(@PathVariable Long id){
        if (alertService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        else
            return ResponseEntity.notFound().build();
    }
    
}
