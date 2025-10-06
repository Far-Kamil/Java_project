package com.needed.task.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.needed.task.model.Alert;
import com.needed.task.repository.AlertRepository;


import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AlertService {
    private final AlertRepository alertRepository;
    public AlertService(AlertRepository alertRepository)
    {
        this.alertRepository=alertRepository;
    }
    @Transactional
    @CacheEvict(value = {"allerts", "allert"}, allEntries = true)
    public Alert create(Alert alert)
    {
        return alertRepository.save(alert);
    }
    @Cacheable(value = "alerts", key = "#root.methodName")
    public List<Alert> getAll()
    {
        return alertRepository.findAll();
    }
    @Cacheable(value = "products", key = "#id")
    public Alert getById(Long id)
    {
        return alertRepository.findById(id).orElse(null);
    }
     @Transactional
    public boolean delete(Long id)
    {
        if (alertRepository.existsById(id)) {
            alertRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
