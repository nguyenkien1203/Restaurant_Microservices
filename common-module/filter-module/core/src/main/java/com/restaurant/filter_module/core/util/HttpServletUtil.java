package com.restaurant.filter_module.core.util;


import static com.restaurant.filter_module.core.constants.SecurityConstants.REQUEST_ID;

import com.restaurant.filter_module.core.constants.SecurityConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;


/**
 * The interface Http servlet util.
 */
public interface HttpServletUtil {
    /**
     * The constant REQUEST_ID_LOG.
     */
    String REQUEST_ID_LOG = "requestId";

    /**
     * Gets remote ip.
     *
     * @param httpServletRequest the http servlet request
     * @return the remote ip
     */
    static List<String> getRemoteIp(HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null) {
            return new ArrayList<>();
        }
        String ipAddress = httpServletRequest.getHeader(SecurityConstants.CLIENT_IP);
        if (ipAddress == null) {
            String remoteAddr = httpServletRequest.getRemoteAddr();
            if (remoteAddr == null) {
                return new ArrayList<>();
            }
            return List.of(httpServletRequest.getRemoteAddr());
        }
        return Arrays.asList(ipAddress.trim().split("\\s*,\\s*"));
    }

    /**
     * Gets client ip.
     *
     * @param httpServletRequest the http servlet request
     * @return the client ip
     */
    static String getClientIP(HttpServletRequest httpServletRequest) {
        List<String> remoteIp = getRemoteIp(httpServletRequest);
        if (remoteIp.isEmpty()) {
            return null;
        }
        return remoteIp.getFirst();
    }

    /**
     * Gets request id.
     *
     * @param httpServletRequest the http servlet request
     * @return the request id
     */
    static String getRequestId(HttpServletRequest httpServletRequest) {
        String requestId;
        if (StringUtils.hasText(httpServletRequest.getHeader(REQUEST_ID))) {
            requestId = httpServletRequest.getHeader(REQUEST_ID);
        } else {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }
        MDC.put(REQUEST_ID_LOG, requestId);
        return requestId;
    }
}
