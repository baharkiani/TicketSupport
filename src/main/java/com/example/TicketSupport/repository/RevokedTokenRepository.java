package com.example.TicketSupport.repository;

import com.example.TicketSupport.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Integer> {
    boolean existsByJti(String jti);
}
