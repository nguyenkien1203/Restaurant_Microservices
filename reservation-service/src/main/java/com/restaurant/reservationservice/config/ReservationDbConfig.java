package com.restaurant.reservationservice.config;


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
@EnableJpaRepositories(basePackages = "com.restaurant.reservationservice.repository", entityManagerFactoryRef = "reservationEntityManagerFactory", transactionManagerRef = "reservationTransactionManager")
public class ReservationDbConfig {

    @Primary
    @Bean(name = "reservationDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.reservation")
    public DataSource reservationDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "reservationEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean reservationEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("reservationDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.restaurant.reservationservice.entity")
                .persistenceUnit("reservation")
                .properties(jpaProperties())
                .build();
    }

    @Primary
    @Bean(name = "reservationTransactionManager")
    public PlatformTransactionManager reservationTransactionManager(
            @Qualifier("reservationEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    private Map<String, Object> jpaProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.put("hibernate.hbm2ddl.auto", "update");
        return props;
    }


}
