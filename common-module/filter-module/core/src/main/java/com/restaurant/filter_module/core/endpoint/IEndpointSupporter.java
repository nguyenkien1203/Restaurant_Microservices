package com.restaurant.filter_module.core.endpoint;

import com.restaurant.data.model.IEndpointModel;
import com.restaurant.filter_module.core.exception.FilterException;


/**
 * The interface Endpoint supporter.
 */
public interface IEndpointSupporter {

    /**
     * Gets mid.
     *
     * @param endpoint the endpoint
     * @return the mid
     * @throws FilterException the filter exception
     */
    IEndpointModel getEndpoint(String endpoint) throws FilterException;
}
