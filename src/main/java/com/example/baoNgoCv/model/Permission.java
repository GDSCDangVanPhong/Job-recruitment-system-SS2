package com.example.baoNgoCv.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "permissions")
    private Set<User> users;
    @ManyToMany(mappedBy = "permissions")
    private Set<Company> companies;

    @ManyToMany(mappedBy = "permissions")
    private Set<Admin> admins;

    // Constructors, getters, and setters
    public Permission() {
    }

    public Permission(String name) {
        this.name = name;
    }

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

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
