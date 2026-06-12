package com.example.backend.repository;

import com.example.backend.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Integer> {
    boolean existsByToken(String token);
}
