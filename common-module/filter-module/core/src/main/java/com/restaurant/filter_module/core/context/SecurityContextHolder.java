package com.restaurant.filter_module.core.context;


import lombok.Getter;

import java.util.function.Supplier;


/**
 * The type Security context holder.
 */
@Getter
public abstract class SecurityContextHolder {

    private static int initializeCount = 0;

    private static SecurityContextHolderStrategy strategy;

    static {
        initialize();
    }

    private SecurityContextHolder() {
    }

    private static void initialize() {
        initializeStrategy();
        initializeCount++;
    }

    private static void initializeStrategy() {
        strategy = new ThreadLocalContextHolderStrategy();
    }

    /**
     * Create empty context security context.
     *
     * @return the security context
     */
    public static SecurityContext createEmptyContext() {
        return strategy.createEmptyContext();
    }

    /**
     * Gets context.
     *
     * @return the context
     */
    public static SecurityContext getContext() {
        return strategy.getContext();
    }

    /**
     * Sets context.
     *
     * @param context the context
     */
    public static void setContext(SecurityContext context) {
        strategy.setContext(context);
    }

    /**
     * Gets deferred context.
     *
     * @return the deferred context
     */
    public static Supplier<SecurityContext> getDeferredContext() {
        return strategy.getDeferredContext();
    }

    /**
     * Gets or create context.
     *
     * @return the or create context
     */
    public static SecurityContext getOrCreateContext() {
        SecurityContext securityContext = getContext();
        if (securityContext == null) {
            return createEmptyContext();
        }
        return securityContext;
    }

    /**
     * Clear context.
     */
    public static void clearContext() {
        strategy.clearContext();
    }

    @Override
    public String toString() {
        return "SecurityContextHolder[strategy='"
                + strategy.getClass().getSimpleName()
                + "'; initializeCount="
                + initializeCount + "]";
    }
}
