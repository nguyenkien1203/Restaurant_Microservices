package com.restaurant.filter_module.core.model;

import com.restaurant.filter_module.core.filter.FilterResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 * The type Default filter response.
 */
@Getter
@Setter
@Builder(toBuilder = true)
public class DefaultFilterResponse implements FilterResponse {

    private HttpServletResponse httpServletResponse;
}
