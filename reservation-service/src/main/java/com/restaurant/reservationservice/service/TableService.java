// TableService.java
package com.restaurant.reservationservice.service;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.reservationservice.dto.*;
import com.restaurant.reservationservice.enums.TableStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TableService {

    TableDto createTable(CreateTableRequest request) throws DataFactoryException;

    TableDto getTableById(Long id) throws CacheException, DataFactoryException;

    List<TableDto> getAllTables() throws CacheException, DataFactoryException;

    TableDto updateTable(Long id, UpdateTableRequest request) throws CacheException, DataFactoryException;

    void deleteTable(Long id) throws DataFactoryException;

    TableDto updateTableStatus(Long id, TableStatus status) throws CacheException, DataFactoryException;

    List<TableDto> getAvailableTablesForSlot(LocalDate date, LocalTime startTime,
                                             LocalTime endTime, Integer partySize);
}