package com.restaurant.reservationservice.service.impl;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.reservationservice.dto.CreateTableRequest;
import com.restaurant.reservationservice.dto.TableDto;
import com.restaurant.reservationservice.dto.UpdateTableRequest;
import com.restaurant.reservationservice.entity.TableEntity;
import com.restaurant.reservationservice.enums.ReservationStatus;
import com.restaurant.reservationservice.enums.TableStatus;
import com.restaurant.reservationservice.factory.TableFactory;
import com.restaurant.reservationservice.repository.TableRepository;
import com.restaurant.reservationservice.service.TableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final TableFactory tableFactory;
    private final TableRepository tableRepository;

    @Override
    public TableDto createTable(CreateTableRequest request) throws DataFactoryException {
        log.info("Creating table {}", request.getTableNumber());

        if (tableFactory.existsByTableNumber(request.getTableNumber())) {
            throw new DataFactoryException("Table number already exists: " + request.getTableNumber());
        }

        TableDto dto = TableDto.builder()
                .tableNumber(request.getTableNumber())
                .capacity(request.getCapacity())
                .minCapacity(request.getMinCapacity() != null ? request.getMinCapacity() : 1)
                .status(TableStatus.AVAILABLE)
                .description(request.getDescription())
                .isActive(true)
                .build();

        return tableFactory.create(dto);
    }

    @Override
    @Transactional
    public TableDto getTableById(Long id) throws CacheException, DataFactoryException {
        return tableFactory.getModel(id);
    }

    @Override
    @Transactional
    public List<TableDto> getAllTables() throws CacheException, DataFactoryException {
        return tableFactory.getList();
    }

    @Override
    @Transactional
    public TableDto updateTable(Long id, UpdateTableRequest request) throws CacheException, DataFactoryException {
        log.info("Updating table {}", id);

        TableDto existing = tableFactory.getModel(id, null);

        if (request.getTableNumber() != null && !request.getTableNumber().equals(existing.getTableNumber())) {
            if (tableFactory.existsByTableNumber(request.getTableNumber())) {
                throw new DataFactoryException("Table number already exists: " + request.getTableNumber());
            }
            existing.setTableNumber(request.getTableNumber());
        }

        if (request.getCapacity() != null) {
            existing.setCapacity(request.getCapacity());
        }
        if (request.getMinCapacity() != null) {
            existing.setMinCapacity(request.getMinCapacity());
        }

        if (request.getStatus() != null) {
            existing.setStatus(request.getStatus());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive());
        }

        return tableFactory.update(existing, null);
    }


    /*  Implement soft delete (later)
        publish kafka event to relevant consumer
    * */

    @Override
    @Transactional
    public void deleteTable(Long id) throws DataFactoryException {
        log.info("Deleting table {}", id);
        tableFactory.delete(id);
    }

    @Override
    @Transactional
    public TableDto updateTableStatus(Long id, TableStatus status) throws CacheException, DataFactoryException {
        log.info("Updating status of table {} to {}", id, status);
        TableDto table = tableFactory.getModel(id, null);
        table.setStatus(status);
        return tableFactory.update(table, null);
    }

    @Override
    public List<TableDto> getAvailableTablesForSlot(LocalDate date, LocalTime startTime, LocalTime endTime, Integer partySize) {
        log.info("Getting available tables for {} {}-{} size {}", date, startTime, endTime, partySize);
        List<ReservationStatus> excludedStatuses = Arrays.asList(
                ReservationStatus.CANCELLED,
                ReservationStatus.COMPLETED,
                ReservationStatus.NO_SHOW
        );
        List<TableEntity> entities = tableRepository.findAvailableTables(date, startTime, endTime, partySize, excludedStatuses);
        return entities.stream()
                .map(tableFactory::convertToModel) // if convertToModel is public; otherwise map manually
                .collect(Collectors.toList());
    }
}
