package com.restaurant.filter_module.core.filter;

import com.restaurant.data.model.IEndpointModel;
import jakarta.servlet.http.HttpServletRequest;

/**
 * The interface Filter request.
 */
public interface FilterRequest {
    /**
     * Gets http servlet request.
     *
     * @return the http servlet request
     */
    HttpServletRequest getHttpServletRequest();

    /**
     * Sets metadata.
     *
     * @param request the request
     */
    void setHttpServletRequest(HttpServletRequest request);


    /**
     * Gets endpoint model.
     *
     * @return the endpoint model
     */
    IEndpointModel getEndpointModel();


    /**
     * Sets endpoint model.
     *
     * @param endpointModel the endpoint model
     */
    void setEndpointModel(IEndpointModel endpointModel);
}
