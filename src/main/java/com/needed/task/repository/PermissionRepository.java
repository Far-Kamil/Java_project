package com.needed.task.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.needed.task.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByResourceAndOperation(String resource, String operation);
}
