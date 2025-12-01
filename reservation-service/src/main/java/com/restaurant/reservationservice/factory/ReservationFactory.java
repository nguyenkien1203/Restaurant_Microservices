// ReservationFactory.java
package com.restaurant.reservationservice.factory;

import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.BaseCrudFactory;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.reservationservice.dto.ReservationDto;
import com.restaurant.reservationservice.dto.TableDto;
import com.restaurant.reservationservice.entity.ReservationEntity;
import com.restaurant.reservationservice.entity.TableEntity;
import com.restaurant.reservationservice.enums.ReservationStatus;
import com.restaurant.reservationservice.filter.ReservationFilter;
import com.restaurant.reservationservice.repository.ReservationRepository;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReservationFactory extends BaseCrudFactory<Long, ReservationDto, Long, ReservationEntity, ReservationRepository> {

    protected ReservationFactory(ICacheService iCacheService, ReservationRepository crudRepository) {
        super(iCacheService, crudRepository);
    }

    @Override
    protected ReservationDto convertToModel(ReservationEntity entity) {
        TableDto tableDto = null;
        if (entity.getTable() != null) {
            tableDto = convertTableToDto(entity.getTable());
        }

        return ReservationDto.builder()
                .id(entity.getId())
                .confirmationCode(entity.getConfirmationCode())
                .userId(entity.getUserId())
                .guestName(entity.getGuestName())
                .guestEmail(entity.getGuestEmail())
                .guestPhone(entity.getGuestPhone())
                .table(tableDto)
                .partySize(entity.getPartySize())
                .reservationDate(entity.getReservationDate())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .status(entity.getStatus())
                .specialRequests(entity.getSpecialRequests())
                .preOrderId(entity.getPreOrderId())
                .reminderSent(entity.getReminderSent())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private TableDto convertTableToDto(TableEntity entity) {
        return TableDto.builder()
                .id(entity.getId())
                .tableNumber(entity.getTableNumber())
                .capacity(entity.getCapacity())
                .minCapacity(entity.getMinCapacity())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .build();
    }

    @Override
    protected ReservationEntity createConvertToEntity(ReservationDto model) {
        TableEntity tableEntity = null;
        if (model.getTable() != null && model.getTable().getId() != null) {
            // We only need a reference by id; JPA will treat this as an existing row
            tableEntity = TableEntity.builder()
                    .id(model.getTable().getId())
                    .build();
        }

        return ReservationEntity.builder()
                .id(model.getId())
                .confirmationCode(model.getConfirmationCode())
                .userId(model.getUserId())
                .guestName(model.getGuestName())
                .guestEmail(model.getGuestEmail())
                .guestPhone(model.getGuestPhone())
                .table(tableEntity)
                .partySize(model.getPartySize())
                .reservationDate(model.getReservationDate())
                .startTime(model.getStartTime())
                .endTime(model.getEndTime())
                .status(model.getStatus())
                .specialRequests(model.getSpecialRequests())
                .preOrderId(model.getPreOrderId())
                .reminderSent(model.getReminderSent())
                .build();
    }

    @Override
    protected ReservationEntity updateConvertToEntity(ReservationDto model, ReservationEntity oldEntity) {
        if (model == null || oldEntity == null) {
            return oldEntity;
        }

        if (model.getTable() != null && model.getTable().getId() != null) {
            // Update the table association if provided
            oldEntity.setTable(TableEntity.builder()
                    .id(model.getTable().getId())
                    .build());
        }

        if (model.getPartySize() != null) {
            oldEntity.setPartySize(model.getPartySize());
        }
        if (model.getReservationDate() != null) {
            oldEntity.setReservationDate(model.getReservationDate());
        }
        if (model.getStartTime() != null) {
            oldEntity.setStartTime(model.getStartTime());
        }
        if (model.getEndTime() != null) {
            oldEntity.setEndTime(model.getEndTime());
        }
        if (model.getStatus() != null) {
            oldEntity.setStatus(model.getStatus());
        }
        if (model.getSpecialRequests() != null) {
            oldEntity.setSpecialRequests(model.getSpecialRequests());
        }
        if (model.getPreOrderId() != null) {
            oldEntity.setPreOrderId(model.getPreOrderId());
        }
        if (model.getReminderSent() != null) {
            oldEntity.setReminderSent(model.getReminderSent());
        }

        return oldEntity;
    }

    @Override
    public CacheConfigFactory<ReservationDto> cacheFactory() {
        return new CacheConfigFactory<ReservationDto>() {
            @Override
            public Class<ReservationDto> getModelClass() {
                return ReservationDto.class;
            }

            @Override
            public Duration singleTtl() {
                return Duration.ofMinutes(10);
            }

            @Override
            public Duration cacheListTtl() {
                return Duration.ofMinutes(5);
            }
        };
    }

    @Override
    protected <F extends IFilter> Optional<ReservationEntity> getEntity(Long id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.findById(id);
        }

        if (filter instanceof ReservationFilter rf) {
            if (rf.getConfirmationCode() != null) {
                return crudRepository.findByConfirmationCode(rf.getConfirmationCode());
            }
        }

        throw new DataFactoryException("Please provide id or filter");
    }

    @Override
    protected <F extends IFilter> Iterable<ReservationEntity> getListEntity(F filter) throws DataFactoryException {
        if (filter instanceof ReservationFilter rf) {
            if (rf.getUserId() != null) {
                return crudRepository.findByUserIdOrderByReservationDateDescStartTimeDesc(rf.getUserId());
            }
        }
        return crudRepository.findAll();
    }

    @Override
    public <F extends IFilter> boolean exists(Long id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.existsById(id);
        }

        if (filter instanceof ReservationFilter rf && rf.getConfirmationCode() != null) {
            return crudRepository.existsByConfirmationCode(rf.getConfirmationCode());
        }

        throw new DataFactoryException("Please provide id or filter");
    }

    // Custom methods
    public ReservationDto findByConfirmationCode(String code) throws CacheException, DataFactoryException {
        ReservationFilter filter = ReservationFilter.builder()
                .confirmationCode(code)
                .build();
        return getModel(null, filter);
    }

    public List<ReservationDto> getTodayReservations() {
        List<ReservationStatus> excludedStatuses = Arrays.asList(
                ReservationStatus.CANCELLED,
                ReservationStatus.NO_SHOW
        );
        return crudRepository.findTodayReservations(LocalDate.now(), excludedStatuses)
                .stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }

    public List<ReservationDto> getUpcomingReservations() {
        List<ReservationStatus> excludedStatuses = Arrays.asList(
                ReservationStatus.CANCELLED,
                ReservationStatus.COMPLETED,
                ReservationStatus.NO_SHOW
        );
        return crudRepository.findUpcomingReservations(LocalDate.now(), excludedStatuses)
                .stream()
                .map(this::convertToModel)
                .collect(Collectors.toList());
    }
}