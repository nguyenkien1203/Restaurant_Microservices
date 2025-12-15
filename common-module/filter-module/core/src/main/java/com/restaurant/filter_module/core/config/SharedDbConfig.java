package com.restaurant.filter_module.core.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Reusable configuration for Shared Config database (shared_config_db).
 * Manages EndpointEntity for endpoint configuration across all services.

 */
@Configuration
@EnableTransactionManagement
@ConditionalOnProperty(prefix = "spring.datasource.shared", name = "enabled", havingValue = "true")
@EnableJpaRepositories(basePackages = "com.restaurant.filter_module.core.repository", entityManagerFactoryRef = "sharedEntityManagerFactory", transactionManagerRef = "sharedTransactionManager")
public class SharedDbConfig {

    @Bean(name = "sharedDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.shared")
    public DataSource sharedDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "sharedEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sharedEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("sharedDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.restaurant.filter_module.core.entity")
                .persistenceUnit("shared")
                .properties(jpaProperties())
                .build();
    }

    @Bean(name = "sharedTransactionManager")
    public PlatformTransactionManager sharedTransactionManager(
            @Qualifier("sharedEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        return props;
    }
}
