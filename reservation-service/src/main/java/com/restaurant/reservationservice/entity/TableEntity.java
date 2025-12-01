package com.restaurant.reservationservice.entity;

import com.restaurant.data.entity.IBaseEntity;
import com.restaurant.reservationservice.enums.TableStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// TableEntity.java
@Entity
@Table(name = "restaurant_tables")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableEntity implements IBaseEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "table_number", unique = true, nullable = false)
    private String tableNumber;  // e.g., "T1", "T2", "VIP-1"

    @Column(nullable = false)
    private Integer capacity;    // Max seats

    @Column(name = "min_capacity")
    private Integer minCapacity; // Min seats (for optimization)


    @Enumerated(EnumType.STRING)
    private TableStatus status;

    private String description;

    @Column(name = "is_active")
    private Boolean isActive = true;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
