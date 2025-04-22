package com.hunterdowney.hdowneyinventoryapp.security;

import jakarta.persistence.*;

@Entity
public class ApiToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    public ApiToken() { }
    public ApiToken(String token, UserRole role) {
        this.token = token;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}