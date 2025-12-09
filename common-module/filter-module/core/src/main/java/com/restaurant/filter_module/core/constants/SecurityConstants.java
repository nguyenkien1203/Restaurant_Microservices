package com.restaurant.filter_module.core.constants;


/**
 * The type Security constants.
 */
public abstract class SecurityConstants {
    /**
     * The constant CLIENT_IP.
     */
    public static final String CLIENT_IP = "X-Forwarded-For";
    /**
     * The constant X_REQUEST_ID.
     */
    public static final String REQUEST_ID = "X-Request-ID";
    /**
     * The constant IS_ENCRYPT_HEADER.
     */
    public static final String IS_ENCRYPT_HEADER = "x-is-encrypt";
    /**
     * The constant X_KEY_ID.
     */
    public static final String X_KEY_ID = "x-key-id";

    private SecurityConstants() {
    }
}
