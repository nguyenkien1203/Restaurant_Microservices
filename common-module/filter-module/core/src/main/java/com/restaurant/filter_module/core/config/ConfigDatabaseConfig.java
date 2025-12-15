package com.restaurant.filter_module.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Default configuration for endpoint config entities and repositories.
 * Uses the primary datasource of each service.
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.shared", name = "enabled", havingValue = "false", matchIfMissing = true)
@EntityScan(basePackages = "com.restaurant.filter_module.core.entity")
@EnableJpaRepositories(basePackages = "com.restaurant.filter_module.core.repository")
public class ConfigDatabaseConfig {
    // Uses primary datasource - each service has its own endpoint_config table
}
