package com.restaurant.authservice.repository;

import com.restaurant.authservice.entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthEntity, Long> {

    Optional<AuthEntity> findByEmail(String email);

   boolean existsByEmail(String email);

}
