package com.needed.task.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.needed.task.enums.StatusType;
import com.needed.task.exception.AlertNotFoundException;
import com.needed.task.model.Alert;
import com.needed.task.repository.AlertRepository;




@Service
@Transactional
public class CachedAlertService implements AlertService{
    private final AlertRepository alertRepository;
    public CachedAlertService(AlertRepository alertRepository)
    {
        this.alertRepository=alertRepository;
    }
    
    @Override
    @Transactional(readOnly=true)
    @Cacheable(value = "alert", unless="#result.isEmpty()")
    public List <Alert> findAll()
    {
        return alertRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "alertsByStatus", key = "#status.name()")
    public List <Alert> findByStatus(StatusType status)
    {
        return alertRepository.findByStatus(status);
    }

    @Override 
    @Transactional(readOnly = true)
    public Optional<Alert> findById(Long id) 
    {
        return alertRepository.findById(id);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true)
    })
    public Alert create(Alert alert) 
    {
        if (alert.getTimestamp() == null) 
        {
            alert.setTimestamp(java.time.LocalDateTime.now());
        }
        if (alert.getStatus() == null) 
        {
            alert.setStatus(StatusType.NEW);
        }
        return alertRepository.save(alert);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true)
    })
    public Alert updateStatus(Long alertId, StatusType newStatus) 
    {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.setStatus(newStatus);
        return alertRepository.save(alert);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true)
    })
    public void deleteById(Long id) 
    {
        if (!alertRepository.existsById(id)) 
        {
            throw new AlertNotFoundException(id);
        }
        alertRepository.deleteById(id);
    }

    //Additional methods with caching
    @Transactional(readOnly = true)
    @Cacheable(value = "alertsByBus", key = "#busId")
    public List<Alert> findByBusId(Long busId) 
    {
        return alertRepository.findByBusId(busId);
    }

    //Method for forced cache flushing
     @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true)
    })
    public void clearAllCache() 
    {
        //Method for clearing cache only
    }
}
