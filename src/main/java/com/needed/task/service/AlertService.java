package com.needed.task.service;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import com.needed.task.enums.StatusType;
import com.needed.task.model.Alert;
import org.springframework.transaction.annotation.Transactional;


public interface AlertService {
    @Cacheable(value = "alert", key="#root.methodName")
    List<Alert> findAll();

    @Cacheable(value = "alert", key = "#status")
    List<Alert> findByStatus(StatusType statusType);

    @Cacheable(value = "alert", key = "#id")
    Optional <Alert> findById(Long id);

    @Transactional
    @CacheEvict(value = {"allerts"}, allEntries = true)
    Alert create(Alert alert);

    @Transactional
    @CacheEvict(value = {"alerts","alert"}, allEntries = true)
    Alert updateStatus(Long alertId, StatusType newStatus);
    
    @Transactional
    @CacheEvict(value = {"alerts","alert"}, allEntries = true)
    void deleteById(Long id);
}
