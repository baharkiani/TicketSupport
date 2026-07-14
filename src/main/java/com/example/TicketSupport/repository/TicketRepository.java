package com.example.TicketSupport.repository;

import com.example.TicketSupport.entity.Ticket;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;

@Registered
public interface TicketRepository extends JpaRepository<Ticket,Long> {

}
