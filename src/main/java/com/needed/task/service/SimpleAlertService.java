package com.needed.task.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.needed.task.enums.StatusType;
import com.needed.task.exception.AlertNotFoundException;
import com.needed.task.model.Alert;
import com.needed.task.repository.AlertRepository;


@Service
@Transactional(readOnly= true)
public class SimpleAlertService implements AlertService {
    private final AlertRepository alertRepository;
    
    public SimpleAlertService (AlertRepository alertRepository)
    {
        this.alertRepository=alertRepository;
    }

    @Override
    public List <Alert> findAll()
    {
        return alertRepository.findAll();
    }

    @Override 
    public List <Alert> findByStatus(StatusType statusType)
    {
        return alertRepository.findByStatus(statusType);
    }

    @Override
    public Optional <Alert> findById(Long id)
    {
        if (id == null) {
        throw new IllegalArgumentException("ID алерта не может быть null");
        }
        return alertRepository.findById(id);
    }

    @Override
    public Alert create(Alert alert) {
        if (alert.getTimestamp() == null) {
            alert.setTimestamp(java.time.LocalDateTime.now());
        }
        if (alert.getStatus() == null) {
            alert.setStatus(StatusType.NEW);
        }
        return alertRepository.save(alert);
    }
    
    @Override
    public Alert updateStatus(Long alertId, StatusType statusType)
    {
         if (alertId == null) {
        throw new IllegalArgumentException("ID алерта не может быть null");
        }
        Alert alert= alertRepository.findById(alertId)
        .orElseThrow(()->new AlertNotFoundException(alertId));
        alert.setStatus(statusType);
        return alertRepository.save(alert);
    }

    @Override
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
    public void deleteById(Long id)
    {   
         if (id == null) {
        throw new IllegalArgumentException("ID алерта не может быть null");
    }
        alertRepository.deleteById(id);
    }

   @Override
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
