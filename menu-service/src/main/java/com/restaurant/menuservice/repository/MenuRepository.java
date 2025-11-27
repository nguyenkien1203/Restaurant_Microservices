package com.restaurant.menuservice.repository;

import com.restaurant.menuservice.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, Long> {

    boolean existsByName(String name);
}
