package com.restaurant.factorymodule;

import com.restaurant.data.entity.IBaseEntity;
import com.restaurant.data.model.IBaseModel;
import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.enums.FactoryResponseCode;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.service.ICacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class BaseCrudFactory<I extends Serializable, //id model
        M extends IBaseModel<I>, K extends Serializable, //id entity
        E extends IBaseEntity<K>, R extends CrudRepository<E, K>> extends BaseDataFactory<I, M> {

    /**
     * The Crud repository.
     */
    protected final R crudRepository;

    /**
     * Instantiates a new Base caching factory.
     *
     * @param iCacheService  the cache service
     * @param crudRepository the crud repository
     */
    protected BaseCrudFactory(ICacheService iCacheService, R crudRepository) {
        super(iCacheService);
        this.crudRepository = crudRepository;
    }

    @Override
    protected M aroundGetModel(I id, IFilter filter) throws DataFactoryException {
        E entity = getEntity(id, filter).orElseThrow(() -> new DataFactoryException(notFound()));
        M model = convertToModel(entity);
        postGetModel(model, entity);
        return model;
    }

    @Override
    protected M aroundCreate(M model) {
        E entity = crudRepository.save(createConvertToEntity(model));
        model = convertToModel(entity);
        postCreate(model, entity);
        return model;
    }

    @Override
    protected M aroundUpdate(M model, IFilter filter) throws DataFactoryException, CacheException {
        E oldEntity = getEntity(model.getId(), filter).orElseThrow(() -> new DataFactoryException(notFound()));
        oldEntity = crudRepository.save(updateConvertToEntity(model, oldEntity));
        model = convertToModel(oldEntity);
        postUpdate(model, oldEntity);
        return model;
    }

    @Override
    protected void aroundDelete(I id, IFilter filter) throws DataFactoryException {
        E entity = getEntity(id, filter).orElseThrow(() -> new DataFactoryException(notFound()));
        crudRepository.delete(entity);
        postDelete(entity);
    }

    @Override
    protected List<M> aroundGetListModel(IFilter filter) throws CacheException, DataFactoryException {
        Iterable<E> entities = getListEntity(filter);
        List<M> models = new ArrayList<>();
        for (E entity : entities) {
            models.add(convertToModel(entity));
        }
        return models;
    }

    /**
     * Convert to model m.
     *
     * @param entity the entity
     * @return the m the vnpay invalid exception
     */
    protected abstract M convertToModel(E entity);

    /**
     * Convert to entity e.
     *
     * @param model the model
     * @return the e the vnpay invalid exception
     */
    protected abstract E createConvertToEntity(M model);

    /**
     * Update convert to entity e.
     *
     * @param model     the model
     * @param oldEntity the old entity
     * @return the e the vnpay invalid exception
     */
    protected abstract E updateConvertToEntity(M model, E oldEntity);

    /**
     * Post create - Cache the newly created model and clear list cache.
     *
     * @param model  the model
     * @param entity the entity
     */
    protected void postCreate(M model, E entity) {
        try {
            if (model != null && model.getId() != null) {
                cachePutModel(model.getId(), model);
            }
            // Clear list cache as the collection has changed
            clearCacheListModel();
        } catch (Exception e) {
            log.warn("Failed to cache after create. Error: {}", e.getMessage());
        }
    }

    /**
     * Post delete - Clear cache for the deleted model and list cache.
     *
     * @param entity the entity
     */
    protected void postDelete(E entity) {
        try {
            if (entity != null && entity.getId() != null) {
                clearCacheModelByKey(entity.getId());
            }
            // Clear list cache as the collection has changed
            clearCacheListModel();
        } catch (Exception e) {
            log.warn("Failed to clear cache after delete. Error: {}", e.getMessage());
        }
    }

    /**
     * Post get model.
     *
     * @param model  the model
     * @param entity the entity
     */
    protected void postGetModel(M model, E entity) {
    }

    /**
     * Post update - Clear old cache, cache updated model, and clear list cache.
     *
     * @param model  the model
     * @param entity the entity
     * @throws CacheException the cache exception
     */
    protected void postUpdate(M model, E entity) throws CacheException {
        try {
            if (model != null && model.getId() != null) {
                // Clear old cache first
                clearCacheModelByKey(model.getId());
                // Cache the updated model
                cachePutModel(model.getId(), model);
            }
            // Clear list cache as the data has changed
            clearCacheListModel();
        } catch (CacheException ex) {
            log.error("Failed to update cache after update. Error: ", ex);
            throw ex;
        } catch (Exception e) {
            log.error("Unexpected error during cache update: ", e);
            throw new CacheException("Cache update failed", e.getMessage());
        }
    }

    /**
     * Gets entity.
     *
     * @param <F>    the type parameter
     * @param id     the id
     * @param filter the filter
     * @return the entity the vnpay invalid exception
     * @throws DataFactoryException the data factory exception
     */
    protected <F extends IFilter> Optional<E> getEntity(I id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.findById(convertId(id));
        }
        throw new DataFactoryException("pls Override");
    }

    /**
     * Gets list entity.
     *
     * @param <F>    the type parameter
     * @param filter the filter
     * @return the list entity
     * @throws DataFactoryException the data factory exception
     */
    protected <F extends IFilter> Iterable<E> getListEntity(F filter) throws DataFactoryException {
        return crudRepository.findAll();
    }

    @Override
    public Long count() {
        return crudRepository.count();
    }

    @Override
    public boolean exist(I id) throws DataFactoryException {
        return exists(id, null);
    }

    @Override
    public <F extends IFilter> boolean exist(F filter) throws DataFactoryException {
        return exists(null, filter);
    }


    /**
     * Exists boolean.
     *
     * @param <F>    the type parameter
     * @param id     the id
     * @param filter the filter
     * @return the boolean the vnpay invalid exception
     * @throws DataFactoryException the data factory exception
     */
    protected <F extends IFilter> boolean exists(I id, F filter) throws DataFactoryException {
        if (id != null) {
            return crudRepository.existsById(convertId(id));
        }
        throw new DataFactoryException("pls Override");
    }

    /**
     * Convert id .
     *
     * @param id the id
     * @return the
     * @throws DataFactoryException the data factory exception
     */
    protected K convertId(I id) throws DataFactoryException {
        try {
            return (K) id;
        } catch (ClassCastException e) {
            log.error("can not cast I to K pls check entity and @Override method getEntity in factory class");
            throw new DataFactoryException(FactoryResponseCode.CONVERT_ID_FAIL);
        }
    }

}
