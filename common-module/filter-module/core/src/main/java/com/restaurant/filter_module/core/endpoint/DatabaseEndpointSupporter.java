package com.restaurant.filter_module.core.endpoint;

import com.restaurant.data.model.IEndpointModel;
import com.restaurant.filter_module.core.entity.EndpointEntity;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.repository.EndpointRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Database-backed endpoint configuration supporter.

 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseEndpointSupporter implements IEndpointSupporter {

    private final EndpointRepository endpointRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    // Cache endpoints to avoid repeated DB queries
    private List<EndpointEntity> cachedEndpoints = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        refreshCache();
    }

    /**
     * Refresh the endpoint cache from database
     */
    public void refreshCache() {
        cachedEndpoints = endpointRepository.findAll()
                .stream()
                .filter(EndpointEntity::isActive)
                .toList();
        log.info("Loaded {} active endpoint configs from database", cachedEndpoints.size());
    }

    @Override
    public IEndpointModel getEndpoint(String endpoint) throws FilterException {
        log.info("Looking up endpoint config for: {}", endpoint);

        // First try exact match
        for (EndpointEntity entity : cachedEndpoints) {
            if (entity.getEndpoint().equals(endpoint)) {
                log.info("Found exact match: endpoint={}, securityType={}",
                        entity.getEndpoint(), entity.getSecurityType());
                return entity;
            }
        }

        // Then try pattern match using pathPattern column
        for (EndpointEntity entity : cachedEndpoints) {
            String pattern = entity.getPathPattern();
            if (pattern != null && pathMatcher.match(pattern, endpoint)) {
                log.info("Found pattern match: pattern={}, endpoint={}, securityType={}",
                        pattern, endpoint, entity.getSecurityType());
                return entity;
            }
        }

        log.warn("No config found for endpoint: {}, using default PUBLIC", endpoint);
        return createDefaultEndpoint(endpoint);
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
