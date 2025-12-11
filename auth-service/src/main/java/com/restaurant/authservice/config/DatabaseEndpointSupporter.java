package com.restaurant.authservice.config;

import com.restaurant.authservice.entity.EndpointEntity;
import com.restaurant.authservice.repository.EndpointRepository;
import com.restaurant.data.model.IEndpointModel;
import com.restaurant.filter_module.core.endpoint.IEndpointSupporter;
import com.restaurant.filter_module.core.exception.FilterException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseEndpointSupporter implements IEndpointSupporter {

    private final EndpointRepository endpointRepository;

    @Override
    public IEndpointModel getEndpoint(String endpoint) throws FilterException {
        log.info("Looking up endpoint config for: {}", endpoint);

        return endpointRepository.findByEndpointAndIsActiveTrue(endpoint)
                .map(entity -> {
                    log.info("Found endpoint config: endpoint={}, securityType={}", 
                            entity.getEndpoint(), entity.getSecurityType());
                    return (IEndpointModel) entity;
                })
                .orElseGet(() -> {
                    log.warn("No config found for endpoint: {}, using default PUBLIC", endpoint);
                    return createDefaultEndpoint(endpoint);
                });
    }

    /**
     * Create a default endpoint config for unregistered endpoints
     */
    private IEndpointModel createDefaultEndpoint(String endpoint) {
        return EndpointEntity.builder()
                .endpoint(endpoint)
                .pathPattern(endpoint)
                .securityType("PUBLIC")
                .isPublic(true)
                .isActive(true)
                .rateLimit(100L)
                .limitTime(60L)
                .build();
    }
}

