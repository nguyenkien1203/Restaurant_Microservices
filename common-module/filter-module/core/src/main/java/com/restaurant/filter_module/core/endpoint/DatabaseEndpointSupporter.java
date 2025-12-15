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

@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseEndpointSupporter implements IEndpointSupporter {

    private final EndpointRepository endpointRepository;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private List<EndpointEntity> cachedEndpoints = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        refreshCache();
    }

    public void refreshCache() {
        cachedEndpoints = endpointRepository.findAll()
                .stream()
                .filter(EndpointEntity::isActive)
                .toList();
        log.info("Loaded {} active endpoint configs from database", cachedEndpoints.size());
    }


    @Override
    public IEndpointModel getEndpoint(String endpoint, String method) throws FilterException {
        log.debug("Looking up endpoint config for: {} [{}]", endpoint, method);


        for (EndpointEntity entity : cachedEndpoints) {

            // 1. Check url
            boolean isPathMatch = false;
            String pattern = entity.getPathPattern();

            if (pattern != null) {

                isPathMatch = pathMatcher.match(pattern, endpoint);
            }

            if (!isPathMatch) {
                continue;
            }


            String configMethod = entity.getMethod();

            boolean isMethodMatch = "ALL".equalsIgnoreCase(configMethod)
                    || (configMethod != null && configMethod.equalsIgnoreCase(method));


            if (isMethodMatch) {
                log.info("Found match: pattern={} method={}, endpoint={} method={}, securityType={}",
                        pattern, configMethod, endpoint, method, entity.getSecurityType());
                return entity;
            }
        }

        log.warn("No config found for endpoint: {} [{}], using default PUBLIC", endpoint, method);
        return createDefaultEndpoint(endpoint);
    }

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
