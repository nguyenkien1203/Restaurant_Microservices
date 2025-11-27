package com.restaurant.menuservice.service;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.menuservice.dtos.CreateMenuRequest;
import com.restaurant.menuservice.dtos.MenuDto;
import com.restaurant.menuservice.dtos.UpdateMenuRequest;
import com.restaurant.menuservice.filter.MenuFilter;
import com.restaurant.redismodule.exception.CacheException;

import java.util.List;

public interface MenuService {

    /**
     * Create a new menu 
     */
    MenuDto createMenu(CreateMenuRequest request) throws DataFactoryException;

    /**
     * Get menu  by ID
     */
    MenuDto getMenuById(Long id) throws CacheException, DataFactoryException;

    /**
     * Update menu  by ID
     */
    MenuDto updateMenu(Long id, UpdateMenuRequest request) throws DataFactoryException, CacheException;

    /**
     * Delete menu  by ID
     */
    void deleteMenu(Long id) throws DataFactoryException;

    /**
     * Get all menu s with optional filtering
     */
    List<MenuDto> getAllMenus(MenuFilter filter) throws CacheException, DataFactoryException;

    /**
     * Get available menu s (for customers)
     */
    List<MenuDto> getAvailableMenus(String category) throws CacheException, DataFactoryException;

    /**
     * Toggle menu  availability
     */
    MenuDto toggleAvailability(Long id) throws DataFactoryException, CacheException;
}