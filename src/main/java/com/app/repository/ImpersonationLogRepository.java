package com.app.repository;

import com.app.entity.ImpersonationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ImpersonationLogRepository extends JpaRepository<ImpersonationLog, UUID> {

    @Query("SELECT il FROM ImpersonationLog il WHERE il.adminId = :adminId ORDER BY il.createdAt DESC")
    List<ImpersonationLog> findByAdminIdOrderByCreatedAtDesc(@Param("adminId") UUID adminId);

    @Query("SELECT il FROM ImpersonationLog il WHERE il.impersonatedUserId = :userId ORDER BY il.createdAt DESC")
    List<ImpersonationLog> findByImpersonatedUserIdOrderByCreatedAtDesc(@Param("userId") UUID userId);

    @Query("SELECT il FROM ImpersonationLog il WHERE il.createdAt >= :since ORDER BY il.createdAt DESC")
    List<ImpersonationLog> findRecentLogs(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(il) FROM ImpersonationLog il WHERE il.adminId = :adminId AND il.createdAt >= :since")
    Long countByAdminIdSince(@Param("adminId") UUID adminId, @Param("since") LocalDateTime since);
}
