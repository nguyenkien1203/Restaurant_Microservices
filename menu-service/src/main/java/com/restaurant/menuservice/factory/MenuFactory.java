package com.restaurant.menuservice.factory;

import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.BaseCrudFactory;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.menuservice.dtos.MenuDto;
import com.restaurant.menuservice.entity.MenuEntity;
import com.restaurant.menuservice.filter.MenuFilter;
import com.restaurant.menuservice.repository.MenuRepository;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class MenuFactory extends BaseCrudFactory<Long, MenuDto, Long, MenuEntity, MenuRepository> {

    protected MenuFactory(ICacheService iCacheService, MenuRepository crudRepository) {
        super(iCacheService, crudRepository);
    }

    @Override
    protected MenuDto convertToModel(MenuEntity entity) {
        return MenuDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .category(entity.getCategory())
                .imageUrl(entity.getImageUrl())
                .isAvailable(entity.getIsAvailable())
                .preparationTime(entity.getPreparationTime())
                .calories(entity.getCalories())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();

    }

    @Override
    protected MenuEntity createConvertToEntity(MenuDto model) {
        return MenuEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .price(model.getPrice())
                .category(model.getCategory())
                .imageUrl(model.getImageUrl())
                .isAvailable(model.getIsAvailable())
                .preparationTime(model.getPreparationTime())
                .calories(model.getCalories())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .build();
    }

    @Override
    protected MenuEntity updateConvertToEntity(MenuDto model, MenuEntity oldEntity) {
        if(model == null || oldEntity == null){
            return oldEntity;
        }
        if (model.getName() != null) {
            oldEntity.setName(model.getName());
        }
        if (model.getDescription() != null) {
            oldEntity.setDescription(model.getDescription());
        }
        if (model.getPrice() != null) {
            oldEntity.setPrice(model.getPrice());
        }
        if (model.getCategory() != null) {
            oldEntity.setCategory(model.getCategory());
        }
        if (model.getImageUrl() != null) {
            oldEntity.setImageUrl(model.getImageUrl());
        }
        if (model.getIsAvailable() != null) {
            oldEntity.setIsAvailable(model.getIsAvailable());
        }
        if (model.getPreparationTime() != null) {
            oldEntity.setPreparationTime(model.getPreparationTime());
        }
        if (model.getCalories() != null) {
            oldEntity.setCalories(model.getCalories());
        }
        return oldEntity;
    }


    @Override
    public CacheConfigFactory<MenuDto> cacheFactory() {
        return new CacheConfigFactory<MenuDto>() {
            @Override
            public Class<MenuDto> getModelClass() {
                return MenuDto.class;
            }

            @Override
            public Duration singleTtl() {
                return Duration.ofMinutes(60);  // Cache single menu item for 60 minutes
            }

            @Override
            public Duration cacheListTtl() {
                return Duration.ofMinutes(30);  // Cache list for 30 minutes
            }
        };
    }

    @Override
    public <F extends IFilter> MenuDto getModel(Long id, F filter) throws CacheException, DataFactoryException {
        Long resolvedId = id;

        if (resolvedId == null && filter instanceof MenuFilter menuFilter) {
            resolvedId = menuFilter.getId();
        }

        if (resolvedId != null) {
            return super.getModel(resolvedId, filter);
        }

        throw new DataFactoryException("Please provide id or filter");
    }



    @Override
    public <F extends IFilter> boolean exists(Long id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.existsById(id);
        }

        if (filter instanceof MenuFilter menuFilter) {
            if (menuFilter.getName() != null) {
                return crudRepository.existsByName(menuFilter.getName());
            }
        }

        throw new DataFactoryException("Please provide id or filter with name");
    }

}
