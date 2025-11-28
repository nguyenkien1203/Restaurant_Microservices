package com.restaurant.menuservice.service.impl;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.menuservice.dtos.CreateMenuRequest;
import com.restaurant.menuservice.dtos.MenuDto;
import com.restaurant.menuservice.dtos.UpdateMenuRequest;
import com.restaurant.menuservice.factory.MenuFactory;
import com.restaurant.menuservice.filter.MenuFilter;
import com.restaurant.menuservice.service.MenuService;
import com.restaurant.redismodule.exception.CacheException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuFactory menuFactory;

    @Override
    @Transactional
    public MenuDto createMenu(CreateMenuRequest request) throws DataFactoryException {
        log.info("Creating menu item: {}", request.getName());
        MenuFilter menuFilter = MenuFilter.builder()
                .name(request.getName())
                .build();
        if (menuFactory.exists(null, menuFilter)) {
            log.error("Menu item already exists with name: {}", request.getName());
            throw new DataFactoryException("Menu item already exists");
        }
        MenuDto menuItemDto = MenuDto.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .category(request.getCategory())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.getIsAvailable())
                .preparationTime(request.getPreparationTime())
                .calories(request.getCalories())
                .build();

        return menuFactory.create(menuItemDto);
    }

    @Override
    @Transactional
    public MenuDto getMenuById(Long id) throws CacheException, DataFactoryException {
        log.info("Getting menu item by ID: {}", id);
        return menuFactory.getModel(id);
    }

    @Override
    @Transactional
    public MenuDto updateMenu(Long id, UpdateMenuRequest request) throws DataFactoryException, CacheException {
        log.info("Updating menu item with id: {}", id);

        if (!menuFactory.exists(id, null)) {
            log.error("Menu item not found with id: {}", id);
            throw new DataFactoryException("Menu item not found with id: " + id);
        }
        MenuDto existingMenuItem = menuFactory.getModel(id, null);

        // Update only non-null fields
        if (request.getName() != null) {
            existingMenuItem.setName(request.getName());
        }
        if (request.getDescription() != null) {
            existingMenuItem.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            existingMenuItem.setPrice(request.getPrice());
        }
        if (request.getCategory() != null) {
            existingMenuItem.setCategory(request.getCategory());
        }
        if (request.getImageUrl() != null) {
            existingMenuItem.setImageUrl(request.getImageUrl());
        }
        if (request.getIsAvailable() != null) {
            existingMenuItem.setIsAvailable(request.getIsAvailable());
        }
        if (request.getPreparationTime() != null) {
            existingMenuItem.setPreparationTime(request.getPreparationTime());
        }
        if (request.getCalories() != null) {
            existingMenuItem.setCalories(request.getCalories());
        }

        return menuFactory.update(existingMenuItem, null);
    }

    @Override
    @Transactional
    public void deleteMenu(Long id) throws DataFactoryException {
        log.info("Delete item with id: {}", id);
        menuFactory.delete(id);

    }

    @Override
    public List<MenuDto> getAllMenus(MenuFilter filter) throws CacheException, DataFactoryException {
        log.info("Getting all menu items with filter");
        return menuFactory.getList(filter);
    }

    @Override
    public List<MenuDto> getAvailableMenus(String category) throws CacheException, DataFactoryException {
        log.info("Getting available menu items for category: {}", category);
        MenuFilter filter = MenuFilter.builder()
                .category(category)
                .isAvailable(true)
                .build();
        return menuFactory.getList(filter);
    }

    @Override
    public MenuDto toggleAvailability(Long id) throws DataFactoryException, CacheException {
        log.info("Toggling availability for menu item: {}", id);

        MenuDto menuItem = menuFactory.getModel(id);
        menuItem.setIsAvailable(!menuItem.getIsAvailable());

        return menuFactory.update(menuItem);
    }
}
