package com.needed.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ch.qos.logback.core.subst.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
