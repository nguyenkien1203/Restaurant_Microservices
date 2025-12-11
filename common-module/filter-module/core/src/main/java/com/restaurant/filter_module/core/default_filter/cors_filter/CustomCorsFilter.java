package com.restaurant.filter_module.core.default_filter.cors_filter;

import com.restaurant.data.properties.SecurityProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Objects;


/**
 * The type Custom cors filter.
 */
public class CustomCorsFilter extends CorsFilter {

    private final SecurityProperties securityProperties;

    /**
     *
     * @param configSource       the config source
     * @param securityProperties the security properties
     */
    public CustomCorsFilter(CorsConfigurationSource configSource, SecurityProperties securityProperties) {
        super(configSource);
        this.securityProperties = securityProperties;
    }

    /**
     * Should not filter boolean.
     *
     * @param request the request
     * @return the boolean
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return Objects.isNull(securityProperties.getCors());
    }
}
