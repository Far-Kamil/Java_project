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
        Alert alert= alertRepository.findById(alertId)
        .orElseThrow(()->new AlertNotFoundException(alertId));
        alert.setStatus(statusType);
        return alertRepository.save(alert);
    }

    @Override
    public void deleteById(Long id)
    {
        alertRepository.deleteById(id);
    }
}
