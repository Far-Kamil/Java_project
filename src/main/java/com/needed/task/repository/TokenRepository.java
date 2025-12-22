package com.needed.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.needed.task.model.Token;



@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
}
