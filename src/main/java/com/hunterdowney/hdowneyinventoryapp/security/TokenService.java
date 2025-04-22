package com.hunterdowney.hdowneyinventoryapp.security;

import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
public class TokenService {
    private final ApiTokenRepository repo;

    public TokenService(ApiTokenRepository repo) {
        this.repo = repo;
    }

    public UserRole roleFor(String header) {
        if (header == null || header.isBlank()) {
            return UserRole.ANONYMOUS;
        }
        return repo.findByToken(header)
                .map(ApiToken::getRole)
                .orElse(UserRole.ANONYMOUS);
    }

    public void requireRole(String header, UserRole... allowed) {
        UserRole r = roleFor(header);
        for (UserRole a : allowed) {
            if (r == a) {
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Insufficient privileges");
    }
}