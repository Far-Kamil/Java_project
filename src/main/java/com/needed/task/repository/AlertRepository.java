package com.needed.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.needed.task.enums.StatusType;
import com.needed.task.model.Alert;

@Repository
public interface AlertRepository 
    extends JpaRepository <Alert, Long>, JpaSpecificationExecutor<Alert>{
    
    List<Alert> findByStatus(StatusType status);

    List<Alert> findByBusId(Long busId);
    
    List<Alert> findByAssignedToUserId(Long userId);
}
