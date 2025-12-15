package com.restaurant.filter_module.core.endpoint;

import com.restaurant.data.model.IEndpointModel;
import lombok.extern.slf4j.Slf4j;
import com.restaurant.filter_module.core.exception.FilterException;


/**
 * The type Un handle endpoint supporter.
 */
@Slf4j
public class UnHandleEndpointSupporter implements IEndpointSupporter {

    /**
     * Instantiates a new Un handle
     *
     * @throws FilterException the filter exception
     */
    public UnHandleEndpointSupporter() throws FilterException {
        log.error("Need to override IMidSupporter Component");
        throw new FilterException("Need to override IMidSupporter Component");
    }

    @Override
    public IEndpointModel getEndpoint(String endpoint, String method) throws FilterException {
        return null;
    }
}
