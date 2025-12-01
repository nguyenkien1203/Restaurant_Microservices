// TableFactory.java
package com.restaurant.reservationservice.factory;

import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.BaseCrudFactory;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.reservationservice.dto.TableDto;
import com.restaurant.reservationservice.entity.TableEntity;
import com.restaurant.reservationservice.enums.ReservationStatus;
import com.restaurant.reservationservice.repository.TableRepository;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TableFactory extends BaseCrudFactory<Long, TableDto, Long, TableEntity, TableRepository> {

    protected TableFactory(ICacheService iCacheService, TableRepository crudRepository) {
        super(iCacheService, crudRepository);
    }

    @Override
    public TableDto convertToModel(TableEntity entity) {
        return TableDto.builder()
                .id(entity.getId())
                .tableNumber(entity.getTableNumber())
                .capacity(entity.getCapacity())
                .minCapacity(entity.getMinCapacity())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected TableEntity createConvertToEntity(TableDto model) {
        return TableEntity.builder()
                .id(model.getId())
                .tableNumber(model.getTableNumber())
                .capacity(model.getCapacity())
                .minCapacity(model.getMinCapacity())
                .status(model.getStatus())
                .description(model.getDescription())
                .isActive(model.getIsActive())
                .build();
    }

    @Override
    protected TableEntity updateConvertToEntity(TableDto model, TableEntity oldEntity) {
        if (model == null || oldEntity == null) {
            return oldEntity;
        }

        if (model.getTableNumber() != null) {
            oldEntity.setTableNumber(model.getTableNumber());
        }
        if (model.getCapacity() != null) {
            oldEntity.setCapacity(model.getCapacity());
        }
        if (model.getMinCapacity() != null) {
            oldEntity.setMinCapacity(model.getMinCapacity());
        }
        if (model.getStatus() != null) {
            oldEntity.setStatus(model.getStatus());
        }
        if (model.getDescription() != null) {
            oldEntity.setDescription(model.getDescription());
        }
        if (model.getIsActive() != null) {
            oldEntity.setIsActive(model.getIsActive());
        }

        return oldEntity;
    }

    @Override
    public CacheConfigFactory<TableDto> cacheFactory() {
        return new CacheConfigFactory<TableDto>() {
            @Override
            public Class<TableDto> getModelClass() {
                return TableDto.class;
            }

            @Override
            public Duration singleTtl() {
                return Duration.ofMinutes(30);
            }

            @Override
            public Duration cacheListTtl() {
                return Duration.ofMinutes(15);
            }
        };
    }

    @Override
    protected <F extends IFilter> Optional<TableEntity> getEntity(Long id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.findById(id);
        }
        throw new DataFactoryException("Please provide id");
    }

    @Override
    protected <F extends IFilter> Iterable<TableEntity> getListEntity(F filter) throws DataFactoryException {
        return crudRepository.findByIsActiveTrueOrderByTableNumber();
    }

    @Override
    public <F extends IFilter> boolean exists(Long id, F filter) throws DataFactoryException {

        if (id != null) {
            return crudRepository.existsById(id);
        }
        throw new DataFactoryException("Please provide id");
    }

    // Custom methods
    public List<TableDto> getAvailableTables(LocalDate date, LocalTime startTime,
                                             LocalTime endTime, Integer partySize) {
        List<TableEntity> tables;

        List<ReservationStatus> excludedStatuses = Arrays.asList(
                ReservationStatus.CANCELLED,
                ReservationStatus.COMPLETED,
                ReservationStatus.NO_SHOW
        );

        tables = crudRepository.findAvailableTables(date, startTime, endTime, partySize, excludedStatuses);

        return tables.stream().map(this::convertToModel).collect(Collectors.toList());
    }

    public boolean existsByTableNumber(String tableNumber) {
        return crudRepository.existsByTableNumber(tableNumber);
    }
}