package com.restaurant.authservice.repository;

import com.restaurant.authservice.entity.SessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<SessionEntity, String> {

    @Query("SELECT s FROM SessionEntity s WHERE s.id = :authId AND s.isActive = true AND s.logoutAt IS NULL AND s.expiresAt > :now")
    Optional<SessionEntity> findActiveSession(@Param("authId") String authId, @Param("now") LocalDateTime now);

    default Optional<SessionEntity> findActiveSession(String authId) {
        return findActiveSession(authId, LocalDateTime.now());
    }

    @Modifying
    @Query("UPDATE SessionEntity s SET s.logoutAt = :logoutTime, s.isActive = false WHERE s.id = :authId")
    void revokeSession(@Param("authId") String authId, @Param("logoutTime") LocalDateTime logoutTime);

    @Modifying
    @Query("UPDATE SessionEntity s SET s.logoutAt = :logoutTime, s.isActive = false WHERE s.userId = :userId AND s.logoutAt IS NULL")
    void revokeAllUserSessions(@Param("userId") Long userId, @Param("logoutTime") LocalDateTime logoutTime);

    @Query("SELECT COUNT(s) FROM SessionEntity s WHERE s.userId = :userId AND s.isActive = true AND s.logoutAt IS NULL")
    long countActiveSessionsByUserId(@Param("userId") Long userId);

    List<SessionEntity> findByUserIdAndIsActiveTrueAndLogoutAtIsNull(Long userId);

    List<SessionEntity> findByExpiresAtBeforeAndIsActiveTrue(LocalDateTime now);
}
