package com.app.repository;

import com.app.entity.Delegation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DelegationRepository extends JpaRepository<Delegation, UUID> {

    @Query("SELECT d FROM Delegation d WHERE d.toUserId = :userId AND d.active = true")
    List<Delegation> findActiveDelegationsByUserId(@Param("userId") UUID userId);

    @Query("SELECT d FROM Delegation d WHERE d.toUserId = :userId AND d.active = true AND d.permission = :permission")
    Delegation findActiveDelegationByUserIdAndPermission(@Param("userId") UUID userId, @Param("permission") Delegation.Permission permission);

    @Query("SELECT d FROM Delegation d WHERE d.fromUserId = :userId")
    List<Delegation> findDelegationsByFromUserId(@Param("userId") UUID userId);

    @Query("SELECT d FROM Delegation d WHERE d.toUserId = :userId")
    List<Delegation> findDelegationsByToUserId(@Param("userId") UUID userId);
}
