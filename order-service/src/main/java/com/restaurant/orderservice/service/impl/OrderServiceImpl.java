package com.restaurant.orderservice.service.impl;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.orderservice.dto.*;
import com.restaurant.orderservice.enums.OrderStatus;
import com.restaurant.orderservice.enums.OrderType;
import com.restaurant.orderservice.enums.PaymentStatus;
import com.restaurant.orderservice.exception.MenuItemNotFoundException;
import com.restaurant.orderservice.exception.MenuServiceException;
import com.restaurant.orderservice.factory.OrderFactory;
import com.restaurant.orderservice.filter.OrderFilter;
import com.restaurant.orderservice.service.MenuServiceClient;
import com.restaurant.orderservice.service.OrderService;
import com.restaurant.redismodule.exception.CacheException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderFactory orderFactory;
    private final MenuServiceClient menuServiceClient;

    @Override
    public OrderDto createOrder(CreateOrderRequest request, Long userId) throws DataFactoryException {
        log.info("Create order with orderId");

        validateOrderRequest(request);

        List<OrderItemDto> orderItems = buildOrderItems(request.getItems());
        BigDecimal totalAmount = calculateTotal(orderItems);

        OrderDto orderDto = OrderDto.builder()
                .userId(userId)
                .orderType(request.getOrderType())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(totalAmount)
                .deliveryAddress(request.getDeliveryAddress())
                .reservationId(request.getReservationId())
                .notes(request.getNotes())
                .orderItems(orderItems)
                .build();

        OrderDto createdOrder = orderFactory.create(orderDto);
        orderProducerService.publishOrderCreatedEvent(createdOrder);
        return createdOrder;
    }



    @Override
    public OrderDto createGuestOrder(CreateOrderRequest request) throws DataFactoryException {
        log.info("Create order with orderId");

        validateOrderRequest(request);

        List<OrderItemDto> orderItems = buildOrderItems(request.getItems());
        BigDecimal totalAmount = calculateTotal(orderItems);

        OrderDto orderDto = OrderDto.builder()
                .guestEmail(request.getGuestEmail())
                .guestPhone(request.getGuestPhone())
                .guestName(request.getGuestName())
                .orderType(request.getOrderType())
                .status(OrderStatus.PENDING)
                .paymentStatus(PaymentStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(totalAmount)
                .deliveryAddress(request.getDeliveryAddress())
                .notes(request.getNotes())
                .orderItems(orderItems)
                .build();

        OrderDto createdOrder = orderFactory.create(orderDto);
        orderProducerService.publishOrderCreatedEvent(createdOrder);
        return createdOrder;
    }

    @Override
    public OrderDto getOrderById(Long id) throws CacheException, DataFactoryException {
        return null;
    }

    @Override
    public List<OrderDto> getMyOrders(Long userId, OrderFilter filter) throws CacheException, DataFactoryException {
        return List.of();
    }

    @Override
    public OrderDto updateOrder(Long id, UpdateOrderRequest request, Long userId) throws CacheException, DataFactoryException {
        return null;
    }

    @Override
    public void cancelOrder(Long id, Long userId) throws CacheException, DataFactoryException {

    }

    @Override
    public List<OrderDto> getAllOrders(OrderFilter filter) throws CacheException, DataFactoryException {
        return List.of();
    }

    @Override
    public OrderDto updateOrderStatus(Long id, UpdateOrderStatusRequest request) throws CacheException, DataFactoryException {
        return null;
    }

    @Override
    public OrderDto assignDriver(Long orderId, AssignDriverRequest request) throws CacheException, DataFactoryException {
        return null;
    }

    @Override
    public List<OrderDto> getKitchenQueue() throws CacheException, DataFactoryException {
        return List.of();
    }

    @Override
    public List<OrderDto> getDriverAssignedOrders(Long driverId) throws CacheException, DataFactoryException {
        return List.of();
    }

    @Override
    public OrderDto markOutForDelivery(Long orderId, Long driverId) throws CacheException, DataFactoryException {
        return null;
    }

    @Override
    public OrderDto markDelivered(Long orderId, Long driverId) throws CacheException, DataFactoryException {
        return null;
    }

    @Override
    public OrderDto createPreOrder(Long reservationId, CreateOrderRequest request, Long userId) throws DataFactoryException {
        return null;
    }

    /* ==================HELPER METHOD ============================================ */

    private BigDecimal calculateTotal(List<OrderItemDto> orderItems) {
        return orderItems.stream()
                .map(OrderItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<OrderItemDto> buildOrderItems(@NotEmpty(message = "Order must have at least one item") @Valid List<CreateOrderItemRequest> items) {

        List<OrderItemDto> orderItems = new ArrayList<>();

        for (CreateOrderItemRequest item : items) {
            try {
                MenuItemDto menuItem = menuServiceClient.getMenuItemById(
                        item.getMenuItemId()
                );
                if (menuItem == null) {
                    throw new MenuItemNotFoundException(item.getMenuItemId());
                }

                if (!Boolean.TRUE.equals(menuItem.getIsAvailable())) {
                    throw new DataFactoryException(
                            "Menu item '" + menuItem.getName() + "' is currently unavailable"
                    );
                }
                // Build order item with actual menu data
                BigDecimal subtotal = menuItem.getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                OrderItemDto orderItem = OrderItemDto.builder()
                        .menuItemId(menuItem.getId())
                        .menuItemName(menuItem.getName())
                        .quantity(item.getQuantity())
                        .unitPrice(menuItem.getPrice())
                        .subtotal(subtotal)
                        .notes(item.getNotes())
                        .build();

                orderItems.add(orderItem);

            } catch (MenuItemNotFoundException e) {
                throw e;
            } catch (Exception e) {
                log.error("Error fetching menu item {}: {}", item.getMenuItemId(), e.getMessage());
                throw new MenuServiceException(
                        "Failed to fetch menu item details for ID: " + item.getMenuItemId(), e
                );
            }
        }
        return orderItems;
    }


    public void validateOrderRequest(CreateOrderRequest request) throws DataFactoryException {
        if (request.getOrderType() == OrderType.DELIVERY &&
                (request.getDeliveryAddress() == null || request.getDeliveryAddress().isBlank())) {
            throw new DataFactoryException("Delivery address is required for delivery orders");
        }

        if (request.getOrderType() == OrderType.PRE_ORDER && request.getReservationId() == null) {
            throw new DataFactoryException("Reservation ID is required for pre-orders");
        }

    }
}
