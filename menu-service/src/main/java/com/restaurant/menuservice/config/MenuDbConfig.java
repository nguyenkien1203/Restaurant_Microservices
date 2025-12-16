package com.restaurant.menuservice.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "com.restaurant.menuservice.repository", entityManagerFactoryRef = "menuEntityManagerFactory", transactionManagerRef = "menuTransactionManager")
public class MenuDbConfig {

    @Primary
    @Bean(name = "menuDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.menu")
    public DataSource menuDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "menuEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean profileEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("menuDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.restaurant.menuservice.entity")
                .persistenceUnit("menu")
                .properties(jpaProperties())
                .build();
    }

    @Primary
    @Bean(name = "menuTransactionManager")
    public PlatformTransactionManager profileTransactionManager(
            @Qualifier("menuEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        return props;
    }
}
