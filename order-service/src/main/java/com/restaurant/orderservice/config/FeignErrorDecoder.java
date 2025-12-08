package com.restaurant.orderservice.config;

import com.restaurant.orderservice.exception.MenuItemNotFoundException;
import com.restaurant.orderservice.exception.MenuServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        log.error("Feign error - Method: {}, Status: {}, Reason: {}",
                methodKey, response.status(), response.reason());

        return switch (response.status()) {
            case 404 -> new MenuItemNotFoundException("Menu item not found");
            case 503 -> new MenuServiceException("Menu service is unavailable");
            default -> defaultDecoder.decode(methodKey, response);
        };
    }
}
