package com.restaurant.profileservice.repository;

import com.restaurant.profileservice.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {

    Optional<ProfileEntity> findByUserId(Long userId);
    Optional<ProfileEntity> findByEmail(String email);
    boolean existsByUserId(Long userId);
}
