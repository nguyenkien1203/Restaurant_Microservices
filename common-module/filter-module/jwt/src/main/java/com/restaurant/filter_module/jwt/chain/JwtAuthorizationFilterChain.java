package com.restaurant.filter_module.jwt.chain;

import com.restaurant.filter_module.core.chain.BaseFilterChain;
import com.restaurant.filter_module.core.chain.MvcFilter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JWT Authorization filter chain.
 *
 * @author namdx @vnpay.vn
 */
@Slf4j
public class JwtAuthorizationFilterChain extends BaseFilterChain implements IJwtFilterChain {

    /**
     * Instantiates a new Base filter chain.
     *
     * @param filters the filters
     */
    public JwtAuthorizationFilterChain(List<MvcFilter> filters) {
        super(filters);
        log.info("----------* Initialize MVC Jwt Authorization filter chain *---------- \n{}", filters);
    }
}
