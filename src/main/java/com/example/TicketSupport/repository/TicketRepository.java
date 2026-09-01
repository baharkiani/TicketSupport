package com.example.TicketSupport.repository;

import com.example.TicketSupport.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TicketRepository extends JpaRepository<Ticket,Long> {
    Page<Ticket> findByDepartmentId(
            Long departmentId,
            Pageable pageable
    );
}
