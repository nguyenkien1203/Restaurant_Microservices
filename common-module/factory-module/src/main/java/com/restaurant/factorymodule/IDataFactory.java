package com.restaurant.factorymodule;

import com.restaurant.data.model.IBaseModel;
import com.restaurant.data.model.IFilter;
import com.restaurant.factorymodule.exception.DataFactoryException;
import com.restaurant.redismodule.exception.CacheException;

import java.io.Serializable;
import java.util.List;

/**
 * The interface Data factory.
 *
 * @param <I> Id
 * @param <M> Model
 */
public interface IDataFactory <I extends Serializable, M extends IBaseModel<I>> {

    /**
     * Count long.
     *
     * @return the long
     */
    Long count();

    /**
     * Exists by filter boolean.
     *
     * @param id the id
     * @return the boolean
     * @throws DataFactoryException the data factory exception
     */


    /**
     * Exist boolean.
     *
     * @param <F>    the type parameter
     * @param filter the filter
     * @return the boolean
     * @throws DataFactoryException the data factory exception
     */
    <F extends IFilter> boolean exist(F filter) throws DataFactoryException;

    /**
     * Create m.
     *
     * @param model the model
     * @return the m
     */
    M create(M model);

    /**
     * Delete.
     *
     * @param id the id
     * @throws DataFactoryException the data factory exception
     */
    void delete(I id) throws DataFactoryException;


    /**
     * Delete.
     *
     * @param <F>    the type parameter
     * @param filter the filter
     * @throws DataFactoryException the data factory exception
     */
    <F extends IFilter> void delete(F filter) throws DataFactoryException;

    /**
     * Gets model.
     *
     * @param id the id
     * @return the model
     * @throws CacheException       the cache exception
     * @throws DataFactoryException the data factory exception
     */
    M getModel(I id) throws CacheException, DataFactoryException;

    /**
     * Gets model.
     *
     * @param <F>    the type parameter
     * @param id     the id
     * @param filter the filter
     * @return the model
     * @throws CacheException       the cache exception
     * @throws DataFactoryException the data factory exception
     */
    <F extends IFilter> M getModel(I id, F filter) throws CacheException, DataFactoryException;

    /**
     * Gets model.
     *
     * @param <F>    the type parameter
     * @param filter the filter
     * @return the model
     * @throws CacheException       the cache exception
     * @throws DataFactoryException the data factory exception
     */
    <F extends IFilter> M getModel(F filter) throws CacheException, DataFactoryException;

    /**
     * Update m.
     *
     * @param model the model
     * @return the m
     * @throws CacheException       the cache exception
     * @throws DataFactoryException the data factory exception
     */
    M update(M model) throws CacheException, DataFactoryException;

    /**
     * Update m.
     *
     * @param <F>     the type parameter
     * @param model   the model
     * @param iFilter the filter
     * @return the m
     * @throws CacheException       the cache exception
     * @throws DataFactoryException the data factory exception
     */
    <F extends IFilter> M update(M model, F iFilter) throws CacheException, DataFactoryException;

    /**
     * Gets list.
     *
     * @param <F>     the type parameter
     * @param iFilter the filter
     * @return the list
     * @throws CacheException       the cache exception
     * @throws DataFactoryException the data factory exception
     */
    <F extends IFilter> List<M> getList(F iFilter) throws CacheException, DataFactoryException;

    /**
     * Gets list.
     *
     * @return the list
     * @throws CacheException       the cache exception
     * @throws DataFactoryException the data factory exception
     */
    List<M> getList() throws CacheException, DataFactoryException;

}
