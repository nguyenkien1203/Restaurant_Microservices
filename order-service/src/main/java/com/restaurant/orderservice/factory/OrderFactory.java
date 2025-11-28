package com.restaurant.orderservice.factory;

import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.BaseCrudFactory;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.orderservice.dto.OrderDto;
import com.restaurant.orderservice.dto.OrderItemDto;
import com.restaurant.orderservice.entity.OrderEntity;
import com.restaurant.orderservice.entity.OrderItemEntity;
import com.restaurant.orderservice.repository.OrderRepository;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
public class OrderFactory extends BaseCrudFactory<Long, OrderDto, Long, OrderEntity, OrderRepository> {


    /**
     * Instantiates a new Base caching factory.
     *
     * @param iCacheService  the cache service
     * @param crudRepository the crud repository
     */
    protected OrderFactory(ICacheService iCacheService, OrderRepository crudRepository) {
        super(iCacheService, crudRepository);
    }


    @Override
    protected OrderDto convertToModel(OrderEntity entity) {
        List<OrderItemDto> itemDtos = entity.getOrderItems() != null
                ? entity.getOrderItems().stream()
                .map(this::convertItemToDto)
                .collect(Collectors.toList())
                : new ArrayList<>();

        return OrderDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .guestEmail(entity.getGuestEmail())
                .guestPhone(entity.getGuestPhone())
                .guestName(entity.getGuestName())
                .orderType(entity.getOrderType())
                .status(entity.getStatus())
                .paymentStatus(entity.getPaymentStatus())
                .paymentMethod(entity.getPaymentMethod())
                .totalAmount(entity.getTotalAmount())
                .deliveryAddress(entity.getDeliveryAddress())
                .reservationId(entity.getReservationId())
                .driverId(entity.getDriverId())
                .notes(entity.getNotes())
                .estimatedReadyTime(entity.getEstimatedReadyTime())
                .actualDeliveryTime(entity.getActualDeliveryTime())
                .orderItems(itemDtos)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }

    private OrderItemDto convertItemToDto(OrderItemEntity entity) {
        return OrderItemDto.builder()
                .id(entity.getId())
                .menuItemId(entity.getMenuItemId())
                .menuItemName(entity.getMenuItemName())
                .quantity(entity.getQuantity())
                .unitPrice(entity.getUnitPrice())
                .subtotal(entity.getSubtotal())
                .notes(entity.getNotes())
                .build();
    }

    @Override
    protected OrderEntity createConvertToEntity(OrderDto model) {
        OrderEntity entity = OrderEntity.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .guestEmail(model.getGuestEmail())
                .guestPhone(model.getGuestPhone())
                .guestName(model.getGuestName())
                .orderType(model.getOrderType())
                .status(model.getStatus())
                .paymentStatus(model.getPaymentStatus())
                .paymentMethod(model.getPaymentMethod())
                .totalAmount(model.getTotalAmount())
                .deliveryAddress(model.getDeliveryAddress())
                .reservationId(model.getReservationId())
                .driverId(model.getDriverId())
                .notes(model.getNotes())
                .estimatedReadyTime(model.getEstimatedReadyTime())
                .actualDeliveryTime(model.getActualDeliveryTime())
                .build();
        if (model.getOrderItems() != null) {
            for (OrderItemDto itemDto : model.getOrderItems()) {
                OrderItemEntity itemEntity = OrderItemEntity.builder()
                        .menuItemId(itemDto.getMenuItemId())
                        .menuItemName(itemDto.getMenuItemName())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .subtotal(itemDto.getSubtotal())
                        .notes(itemDto.getNotes())
                        .build();
                entity.addOrderItem(itemEntity);
            }
        }
        return entity;

    }

    @Override
    protected OrderEntity updateConvertToEntity(OrderDto model, OrderEntity oldEntity) {
        if (model == null || oldEntity == null) {
            return oldEntity;
        }

        if (model.getStatus() != null) {
            oldEntity.setStatus(model.getStatus());
        }
        if (model.getPaymentStatus() != null) {
            oldEntity.setPaymentStatus(model.getPaymentStatus());
        }
        if (model.getPaymentMethod() != null) {
            oldEntity.setPaymentMethod(model.getPaymentMethod());
        }
        if (model.getDeliveryAddress() != null) {
            oldEntity.setDeliveryAddress(model.getDeliveryAddress());
        }
        if (model.getDriverId() != null) {
            oldEntity.setDriverId(model.getDriverId());
        }
        if (model.getNotes() != null) {
            oldEntity.setNotes(model.getNotes());
        }
        if (model.getEstimatedReadyTime() != null) {
            oldEntity.setEstimatedReadyTime(model.getEstimatedReadyTime());
        }
        if (model.getActualDeliveryTime() != null) {
            oldEntity.setActualDeliveryTime(model.getActualDeliveryTime());
        }
        if (model.getTotalAmount() != null) {
            oldEntity.setTotalAmount(model.getTotalAmount());
        }

        return oldEntity;
    }

    @Override
    public CacheConfigFactory<OrderDto> cacheFactory() {
        return new CacheConfigFactory<OrderDto>() {
            @Override
            public Class<OrderDto> getModelClass() {
                return OrderDto.class;
            }

            @Override
            public Duration singleTtl() {
                return Duration.ofMinutes(15); // Cache single order for 15 minutes
            }

            @Override
            public Duration cacheListTtl() {
                return Duration.ofMinutes(5); // Cache list for 5 minutes
            }
        };
    }

    @Override
    protected <F extends IFilter> Optional<OrderEntity> getEntity(Long id, F filter) throws DataFactoryException {
        return super.getEntity(id, filter);
    }
}
