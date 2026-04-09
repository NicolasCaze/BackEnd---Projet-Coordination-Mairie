package com.app.repository;

import com.app.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, UUID> {
    
    boolean existsByToken(String token);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlacklist t WHERE t.expires_at < :dateTime")
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
