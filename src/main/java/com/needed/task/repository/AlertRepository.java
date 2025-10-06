package com.needed.task.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.needed.task.enums.StatusType;
import com.needed.task.model.Alert;

public interface AlertRepository 
    extends JpaRepository <Alert, Long>{
    
    List<Alert> findByStatus(StatusType status);
    List<Alert> findByBusId(Long busId);
}
