package com.restaurant.filter_module.core.chain;

import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.filter.FilterRequest;
import com.restaurant.filter_module.core.filter.FilterResponse;

import java.util.Iterator;
import java.util.List;

/**
 * The type Base filter chain.
 */
public abstract class BaseFilterChain implements MvcFilterChain {

    private final List<MvcFilter> filters;

    /**
     * Instantiates a new Base filter chain.
     *
     * @param filters the filters
     */
    protected BaseFilterChain(List<MvcFilter> filters) {
        this.filters = filters;
    }

    @Override
    public void doFilter(FilterRequest request, FilterResponse response) throws FilterException {
        new VirtualFilterChain(this.filters.iterator()).doFilter(request, response);
    }

    private record VirtualFilterChain(Iterator<MvcFilter> filterIterator) implements MvcFilterChain {
        @Override
        public void doFilter(FilterRequest request, FilterResponse response) throws FilterException {
            if (filterIterator.hasNext()) {
                filterIterator.next().doFilter(request, response, this);
            }
        }
    }
}
