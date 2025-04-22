package com.hunterdowney.hdowneyinventoryapp.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TokenDataLoader implements CommandLineRunner {
    private final ApiTokenRepository repo;

    public TokenDataLoader(ApiTokenRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        if (repo.count() == 0) {
            repo.save(new ApiToken("assoc-token", UserRole.ASSOC));
            repo.save(new ApiToken("mngr-token",  UserRole.MNGR));
            repo.save(new ApiToken("admin-token", UserRole.ADMIN));
            repo.save(new ApiToken("user-token", UserRole.USER));
        }
    }
}