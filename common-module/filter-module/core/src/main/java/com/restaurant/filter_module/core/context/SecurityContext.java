package com.restaurant.filter_module.core.context;

import com.restaurant.data.model.IEndpointModel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * .
 *
 * @author namdx.
 * @created 2/29/2024 - 2:19 PM.
 */
@Setter
@Getter
@Configuration
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SecurityContext {
    private String ipRequest;
    private String requestId;
    private long startTime;
    private IEndpointModel iEndpointModel;
}
