package com.restaurant.filter_module.core.context;

import java.util.function.Supplier;


/**
 * The interface Security context holder strategy.
 */
public interface SecurityContextHolderStrategy {
    /**
     * Clear context.
     */
    void clearContext();

    /**
     * Gets context.
     *
     * @return the context
     */
    SecurityContext getContext();

    /**
     * Sets context.
     *
     * @param context the context
     */
    void setContext(SecurityContext context);

    /**
     * Gets deferred context.
     *
     * @return the deferred context
     */
    default Supplier<SecurityContext> getDeferredContext() {
        return this::getContext;
    }

    /**
     * Sets deferred context.
     *
     * @param deferredContext the deferred context
     */
    default void setDeferredContext(Supplier<SecurityContext> deferredContext) {
        setContext(deferredContext.get());
    }

    /**
     * Create empty context security context.
     *
     * @return the security context
     */
    SecurityContext createEmptyContext();
}
