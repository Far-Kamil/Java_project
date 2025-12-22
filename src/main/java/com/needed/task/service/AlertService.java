package com.needed.task.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.needed.task.enums.StatusType;
import com.needed.task.model.Alert;
import org.springframework.transaction.annotation.Transactional;


@Service
public interface AlertService {
    @Cacheable(value = "alerts", key="#root.methodName")
    List<Alert> findAll();

    @Cacheable(value = "alerts", key = "#status")
    List<Alert> findByStatus(StatusType statusType);


    Optional <Alert> findById(Long id);

    @Transactional
    @CacheEvict(value = {"alerts", "alertsByStatus", "alertsByBus", "alertsByUser"}, allEntries = true)
    Alert create(Alert alert);

    @Transactional
    @CacheEvict(value = {"alerts", "alertsByStatus", "alertsByBus", "alertsByUser"}, allEntries = true)
    Alert updateStatus(Long alertId, StatusType newStatus);

    @Transactional
    @CacheEvict(value = {"alerts", "alertsByStatus", "alertsByBus", "alertsByUser"}, allEntries = true)
    Alert assignToUser(Long alertId, Long userId);
    
    @Transactional
    @CacheEvict(value = {"alerts", "alertsByStatus", "alertsByBus", "alertsByUser"}, allEntries = true)
    void deleteById(Long id);

    Alert addFileToAlert(Long alertId, String filePath);
}
