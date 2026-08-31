package com.example.TicketSupport.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "department")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "department")
    private Set<Ticket> tickets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setTickets(Set<Ticket> tickets) {
        this.tickets = tickets;
    }
}
