package com.app.repository;

import com.app.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    
    @Query("SELECT a FROM AuditLog a WHERE " +
           "(:actorId IS NULL OR a.actor_id = :actorId) AND " +
           "(:targetUserId IS NULL OR a.target_user_id = :targetUserId) AND " +
           "(:action IS NULL OR a.action = :action) AND " +
           "(:dateFrom IS NULL OR a.timestamp >= :dateFrom) AND " +
           "(:dateTo IS NULL OR a.timestamp <= :dateTo)")
    Page<AuditLog> findByFilters(@Param("actorId") UUID actorId,
                                @Param("targetUserId") UUID targetUserId,
                                @Param("action") String action,
                                @Param("dateFrom") LocalDateTime dateFrom,
                                @Param("dateTo") LocalDateTime dateTo,
                                Pageable pageable);
}
