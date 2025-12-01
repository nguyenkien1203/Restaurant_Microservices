// RestaurantSettingsEntity.java
package com.restaurant.reservationservice.entity;

import com.restaurant.data.entity.IBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "restaurant_settings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSettingsEntity implements IBaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "reservation_duration_minutes")
    @Builder.Default
    private Integer reservationDurationMinutes = 120; // 2 hours default

    @Column(name = "buffer_minutes")
    @Builder.Default
    private Integer bufferMinutes = 15; // Time between reservations

    @Column(name = "max_advance_days")
    @Builder.Default
    private Integer maxAdvanceDays = 30; // How far in advance can book

    @Column(name = "min_advance_hours")
    @Builder.Default
    private Integer minAdvanceHours = 2; // Minimum notice required

    @Column(name = "max_party_size")
    @Builder.Default
    private Integer maxPartySize = 20;

    @Column(name = "time_slot_interval_minutes")
    @Builder.Default
    private Integer timeSlotIntervalMinutes = 30; // Available slots every 30 mins

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}