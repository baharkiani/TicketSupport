package com.example.TicketSupport.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "jti", nullable = false, unique = true)
    private String jti;

    @Column(name = "expiry_at", nullable = false)
    private Instant expiryAt;

    public RevokedToken() {
    }

    public RevokedToken(String jti, Instant expiryAt) {
        this.jti = jti;
        this.expiryAt = expiryAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public Instant getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(Instant expiryAt) {
        this.expiryAt = expiryAt;
    }
}