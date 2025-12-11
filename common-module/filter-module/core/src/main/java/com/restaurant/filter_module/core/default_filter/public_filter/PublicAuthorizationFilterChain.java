package com.restaurant.filter_module.core.default_filter.public_filter;

import com.restaurant.filter_module.core.chain.BaseFilterChain;
import com.restaurant.filter_module.core.chain.MvcFilter;
import com.restaurant.utils.MapperUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * The type Public authorization filter chain.
 */
@Slf4j
public class PublicAuthorizationFilterChain extends BaseFilterChain implements IPublicFilterChain {
    /**
     * Instantiates a new Base vnpay filter chain.
     *
     * @param filters the filters
     */
    public PublicAuthorizationFilterChain(List<MvcFilter> filters) {
        super(filters);
        log.info("\n");
        log.info("----------* Initialize MVC Public Authorization filter chain: *---------- \n{}",
                MapperUtil.writeValueAsStringOrDefault(filters)
        );
    }
}
