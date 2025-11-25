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

import jakarta.validation.constraints.NotNull;




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
    public Optional<Alert> findById(@NotNull Long id) 
    {
        if (id == null) {
    throw new IllegalArgumentException("ID алерта не может быть null");
        }
        return alertRepository.findById(id);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true),
        @CacheEvict(value = "alertByUser", allEntries = true)
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
        @CacheEvict(value = "alertsByBus", allEntries = true),
        @CacheEvict(value = "alertByUser", allEntries = true)        
    })
    public Alert updateStatus(Long alertId, StatusType newStatus) 
    {
        if (alertId == null) {
        throw new IllegalArgumentException("ID алерта не может быть null");
        }
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.setStatus(newStatus);
        return alertRepository.save(alert);
    }

     @Override
    @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true),
        @CacheEvict(value = "alertsByUser", allEntries = true)
    })
    public Alert assignToUser(Long alertId, Long userId) {
        if (alertId == null) {
        throw new IllegalArgumentException("ID алерта не может быть null");
        }
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.setAssignedToUserId(userId);
        alert.setStatus(StatusType.IN_PROGRESS);
        return alertRepository.save(alert);
    }

    @Override
    @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true),
        @CacheEvict(value = "alertByUser", allEntries = true)
    })
    public void deleteById(Long id) 
    {
        if (id == null) {
        throw new IllegalArgumentException("ID алерта не может быть null");
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

    @Transactional(readOnly = true)
    @Cacheable(value = "alertsByUser", key = "#userId")
    public List<Alert> findByAssignedToUserId(Long userId) {
        return alertRepository.findByAssignedToUserId(userId);
    }


    //Method for forced cache flushing
     @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true),
        @CacheEvict(value = "alertByUser", allEntries = true)
    })
    public void clearAllCache() 
    {}

     @Override
    @Caching(evict = {
        @CacheEvict(value = "alerts", allEntries = true),
        @CacheEvict(value = "alertsByStatus", allEntries = true),
        @CacheEvict(value = "alertsByBus", allEntries = true),
        @CacheEvict(value = "alertsByUser", allEntries = true)
    })
    public Alert addFileToAlert(Long alertId, String filePath) {
         if (alertId == null) {
        throw new IllegalArgumentException("ID алерта не может быть null");
    }
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new AlertNotFoundException(alertId));
        alert.setImgPath(filePath);
        return alertRepository.save(alert);
    }

 

}
