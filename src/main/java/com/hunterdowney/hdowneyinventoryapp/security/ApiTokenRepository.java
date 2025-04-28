package com.hunterdowney.hdowneyinventoryapp.security;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ApiTokenRepository extends JpaRepository<ApiToken,Long> {
    Optional<ApiToken> findByToken(String token);
}