package com.restaurant.filter_module.core.cors;

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
     * Instantiates a new Vnpay cors filter.
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
     * @throws ServletException the servlet exception
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) throws ServletException {
        return Objects.isNull(securityProperties.getCors());
    }
}
