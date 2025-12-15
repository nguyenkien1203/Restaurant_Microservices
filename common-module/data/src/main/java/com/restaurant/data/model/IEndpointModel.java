package com.restaurant.data.model;


/**
 * The interface Endpoint model.
 */
public interface IEndpointModel {

    /**
     * Gets endpoint.
     *
     * @return the endpoint
     */
    String getEndpoint();

    /**
     * Gets endpoint.
     *
     * @return the endpoint
     */
    default String getEndpointName() {
        return getEndpoint();
    }

    /**
     * Gets channel.
     *
     * @return the channel
     */
    String getChannel();

    /**
     * Gets description.
     *
     * @return the description
     */
    String getDescription();

    /**
     * Gets security type.
     *
     * @return the security type
     */
    String getSecurityType();

    /**
     * Gets rate limit.
     *
     * @return the rate limit
     */
    Long getRateLimit();

    /**
     * Gets limit time.
     *
     * @return the limit time
     */
    Long getLimitTime();

    /**
     * Is public boolean.
     *
     * @return the boolean
     */
    boolean isPublic();

    /**
     * Is active boolean.
     *
     * @return the boolean
     */
    boolean isActive();

    String getMethod();
}
