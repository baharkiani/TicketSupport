package com.example.TicketSupport.dto;

import com.example.TicketSupport.enums.TicketStatus;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public class UpdateTicketStatusRequest {
    @NotNull(message = "status is required field")
    private TicketStatus status;

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }
}
