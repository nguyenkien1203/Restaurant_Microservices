package com.restaurant.factorymodule;

import com.restaurant.data.enums.IBaseErrorCode;
import com.restaurant.data.enums.NotFound;
import com.restaurant.data.model.IBaseModel;
import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;
import com.restaurant.redismodule.factory.CacheConfigFactory;
import com.restaurant.redismodule.service.ICacheService;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

@Slf4j
public abstract class BaseDataFactory<I extends Serializable, M extends IBaseModel<I>> extends BaseCachingFactory<M> implements IDataFactory<I, M> {
    /**
     * Instantiates a new Base caching factory.
     *
     * @param iCacheService the cache service
     */
    protected BaseDataFactory(ICacheService iCacheService) {
        super(iCacheService);
    }


    @Override
    public M create(M model) {
        return aroundCreate(preCreate(model));
    }

    @Override
    public void delete(I id) throws DataFactoryException {
        aroundDelete(id, preDelete(id, null));
    }

    @Override
    public <F extends IFilter> void delete(F filter) throws DataFactoryException {
        aroundDelete(null, preDelete(null, filter));
    }

    @Override
    public M getModel(I id) throws CacheException, DataFactoryException {
        return getModel(id, null);
    }

    @Override
    public <F extends IFilter> M getModel(I id, F filter) throws CacheException, DataFactoryException {
        M model = null;
        if (id != null) {
            model = getCacheModel(id);
        }
        if (model != null) {
            log.info("get from redis cache for key: {}", id);
            return model;
        }
        log.info("get from database for key: {}", id);
        filter = preGetModel(id, filter);
        model = aroundGetModel(id, filter);
        if (id != null && model != null) {
            cachePutModel(id, model);
        }
        return model;
    }

    @Override
    public <F extends IFilter> M getModel(F filter) throws CacheException, DataFactoryException {
        return getModel(null, filter);
    }

    @Override
    public M update(M model) throws CacheException, DataFactoryException {
        return update(model, null);
    }

    @Override
    public <F extends IFilter> M update(M model, F iFilter) throws CacheException, DataFactoryException {
        return aroundUpdate(model, preUpdate(model, iFilter));
    }

    @Override
    public <F extends IFilter> List<M> getList(F iFilter) throws CacheException, DataFactoryException {
        List<M> models = getCacheListModel(iFilter);
        if (!models.isEmpty()) {
            return models;
        }
        models = aroundGetListModel(iFilter);
        if (models != null) {
            cacheListModel(iFilter, models);
        }
        return models;
    }

    @Override
    public List<M> getList() throws CacheException, DataFactoryException {
        return getList(null);
    }

    /**
     * Pre create m.
     *
     * @param model the model
     * @return the m
     */
    protected M preCreate(M model) {
        return model;
    }

    /**
     * Pre get model m.
     *
     * @param <F>     the type parameter
     * @param id      the id
     * @param iFilter the filter
     * @return the m
     */
    protected <F extends IFilter> F preGetModel(I id, F iFilter) {
        return iFilter;
    }

    /**
     * Pre delete m.
     *
     * @param <F>     the type parameter
     * @param id      the id
     * @param iFilter the filter
     * @return the m
     */
    protected <F extends IFilter> F preDelete(I id, F iFilter) {
        return iFilter;
    }

    /**
     * Pre update m.
     *
     * @param <F>     the type parameter
     * @param model   the model
     * @param iFilter the filter
     * @return the m
     */
    protected <F extends IFilter> F preUpdate(M model, F iFilter) {
        return iFilter;
    }

    /**
     * Around create m.
     *
     * @param model the model
     * @return the m the vnpay invalid exception
     */
    protected abstract M aroundCreate(M model);

    /**
     * Around update m.
     *
     * @param model  the model
     * @param filter the filter
     * @return the m the vnpay invalid exception
     * @throws DataFactoryException the data factory exception
     * @throws CacheException       the cache exception
     */
    protected abstract M aroundUpdate(M model, IFilter filter) throws DataFactoryException, CacheException;

    /**
     * Around delete e.
     *
     * @param id     the id
     * @param filter the filter               the vnpay invalid exception
     * @throws DataFactoryException the data factory exception
     */
    protected abstract void aroundDelete(I id, IFilter filter) throws DataFactoryException;

    /**
     * Around get model m.
     *
     * @param id     the id
     * @param filter the filter
     * @return the m the vnpay invalid exception
     * @throws DataFactoryException the data factory exception
     */
    protected abstract M aroundGetModel(I id, IFilter filter) throws DataFactoryException;

    /**
     * Around get list model m.
     *
     * @param filter the filter
     * @return the m
     * @throws DataFactoryException the data factory exception
     */
    protected abstract List<M> aroundGetListModel(IFilter filter) throws CacheException, DataFactoryException;

    /**
     * Not found base error code.
     *
     * @return the base error code
     */
    protected IBaseErrorCode notFound() {
        return NotFound.NOT_FOUND;
    }

    public abstract boolean exist(I id) throws DataFactoryException;
}
