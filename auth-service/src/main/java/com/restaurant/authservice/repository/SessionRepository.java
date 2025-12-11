package com.restaurant.authservice.repository;

import com.restaurant.authservice.entity.SessionEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

// Repository
public interface SessionRepository extends JpaRepository<SessionEntity, String> {

    Optional<SessionEntity> findByIdAndLogoutAtIsNull(String id);

    @Query("SELECT s FROM SessionEntity s WHERE s.id = :authId AND s.isActive = true AND s.logoutAt IS NULL")
    Optional<SessionEntity> findActiveSession(@Param("authId") String authId);

    @Modifying
    @Query("UPDATE SessionEntity s SET s.logoutAt = :logoutTime WHERE s.id = :authId")
    void revokeSession(@Param("authId") String authId, @Param("logoutTime") LocalDateTime logoutTime);

    @Modifying
    @Query("UPDATE SessionEntity s SET s.logoutAt = :logoutTime WHERE s.userId = :userId AND s.logoutAt IS NULL")
    void revokeAllUserSessions(@Param("userId") Long userId, @Param("logoutTime") LocalDateTime logoutTime);
}
