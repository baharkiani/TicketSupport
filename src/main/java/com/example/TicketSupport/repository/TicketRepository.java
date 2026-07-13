package com.example.TicketSupport.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface TicketRepository extends JpaRepository<TicketRepository,Long> {

}
