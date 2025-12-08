package com.restaurant.reservationservice.repository;

import com.restaurant.reservationservice.entity.RestaurantSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RestaurantSettingsRepository extends JpaRepository<RestaurantSettingsEntity, Long> {

    Optional<RestaurantSettingsEntity> findFirstByOrderByIdAsc();
}
