package com.restaurant.menuservice.controller;

import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.menuservice.dtos.CreateMenuRequest;
import com.restaurant.menuservice.dtos.MenuDto;
import com.restaurant.menuservice.dtos.UpdateMenuRequest;
import com.restaurant.menuservice.filter.MenuFilter;
import com.restaurant.menuservice.service.MenuService;
import com.restaurant.redismodule.exception.CacheException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@Slf4j
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MenuDto>> getAvailableMenus(
            @RequestParam(required = false) String category) throws CacheException, DataFactoryException {

        log.info("GET /api/menu/available - category: {}", category);
        List<MenuDto> menus = menuService.getAvailableMenus(category);
        return ResponseEntity.ok(menus);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MenuDto> getMenuById(
            @PathVariable Long id) throws CacheException, DataFactoryException{
        log.info("Get /api/menu/id");
        MenuDto menuDto = menuService.getMenuById(id);
        return ResponseEntity.ok(menuDto);
    }


    // ========== ADMIN ENDPOINTS ==========

    /**
     * Get all menu s with filtering (Admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MenuDto>> getAllMenus()
            throws CacheException, DataFactoryException {

        log.info("GET /api/menu - Admin access with filter");
        List<MenuDto> menus = menuService.getAllMenus(null);
        return ResponseEntity.ok(menus);
    }

    /**
     * Create new menu  (Admin only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuDto> createMenu(
            @Valid @RequestBody CreateMenuRequest request) throws DataFactoryException {

        log.info("POST /api/menu - Creating: {}", request.getName());
        MenuDto menu = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(menu);
    }

    /**
     * Update menu  (Admin only)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuDto> updateMenu(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMenuRequest request) throws CacheException, DataFactoryException {

        log.info("PUT /api/menu/{} - Admin access", id);
        MenuDto updated = menuService.updateMenu(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete menu  (Admin only)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) throws DataFactoryException {

        log.info("DELETE /api/menu/{} - Admin access", id);
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Toggle menu  availability (Admin only)
     */
    @PatchMapping("/{id}/toggle-availability")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuDto> toggleAvailability(@PathVariable Long id)
            throws CacheException, DataFactoryException {

        log.info("PATCH /api/menu/{}/toggle-availability - Admin access", id);
        MenuDto menu = menuService.toggleAvailability(id);
        return ResponseEntity.ok(menu);
    }

}
