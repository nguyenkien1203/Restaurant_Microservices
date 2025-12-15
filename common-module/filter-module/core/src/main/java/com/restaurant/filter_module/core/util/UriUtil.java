package com.restaurant.filter_module.core.util;

import java.net.URI;

import org.springframework.web.util.UriComponentsBuilder;

/**
 * The interface Uri util.
 *
 * @author hoangnlv @vnpay.vn
 */
public interface UriUtil {

    /**
     * Replace query string.
     *
     * @param rawUri the raw uri
     * @return the string
     */
    static String replaceQuery(String rawUri) {
        URI newUri = UriComponentsBuilder
                .fromUriString(rawUri)
                .replaceQuery(null)
                .build()
                .toUri();
        return newUri.getPath();
    }
}
