package com.app.repository;

import com.app.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
    
    boolean existsByToken(String token);
    
    void deleteByExpires_atBefore(LocalDateTime dateTime);
}
