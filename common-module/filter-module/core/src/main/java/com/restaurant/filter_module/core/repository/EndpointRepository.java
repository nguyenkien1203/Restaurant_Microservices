package com.restaurant.filter_module.core.repository;

import com.restaurant.filter_module.core.entity.EndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EndpointRepository extends JpaRepository<EndpointEntity, Long> {

    Optional<EndpointEntity> findByEndpoint(String endpoint);

    Optional<EndpointEntity> findByEndpointAndIsActiveTrue(String endpoint);
}

