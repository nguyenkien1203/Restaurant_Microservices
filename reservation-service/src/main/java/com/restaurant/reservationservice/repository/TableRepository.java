package com.restaurant.reservationservice.repository;

import com.restaurant.reservationservice.entity.TableEntity;
import com.restaurant.reservationservice.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<TableEntity, Long> {

    // Find by table number
    Optional<TableEntity> findByTableNumber(String tableNumber);

    // Check if table number exists
    boolean existsByTableNumber(String tableNumber);

    // Find active tables
    List<TableEntity> findByIsActiveTrueOrderByTableNumber();


    // Find tables by capacity range
    @Query("SELECT t FROM TableEntity t WHERE t.isActive = true " +
            "AND t.capacity >= :minCapacity AND t.minCapacity <= :partySize " +
            "ORDER BY t.capacity ASC")
    List<TableEntity> findSuitableTables(@Param("partySize") Integer partySize,
                                         @Param("minCapacity") Integer minCapacity);

    // Find available tables for a time slot (tables without conflicting reservations)
    @Query("SELECT t FROM TableEntity t WHERE t.isActive = true " +
            "AND t.capacity >= :partySize " +
            "AND t.id NOT IN (" +
            "  SELECT r.table.id FROM ReservationEntity r " +
            "  WHERE r.reservationDate = :date " +
            "  AND r.status NOT IN :excludedStatus " +
            "  AND ((r.startTime < :endTime AND r.endTime > :startTime))" +
            ") ORDER BY t.capacity ASC")
    List<TableEntity> findAvailableTables(
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("partySize") Integer partySize,
            @Param("excludedStatus")List<ReservationStatus> reservationStatuses);

}
