package com.restaurant.filter_module.core.model;

import com.restaurant.data.model.IEndpointModel;
import com.restaurant.filter_module.core.filter.FilterRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * The type Default filter request.
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class DefaultFilterRequest implements FilterRequest {

    private IEndpointModel endpointModel;
    private HttpServletRequest httpServletRequest;
}
