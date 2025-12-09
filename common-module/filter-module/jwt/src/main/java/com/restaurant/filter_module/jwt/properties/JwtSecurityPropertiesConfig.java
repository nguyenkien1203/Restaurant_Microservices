package com.restaurant.filter_module.jwt.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@Data
@Primary
public class JwtSecurityPropertiesConfig {
    @Value("${jwt.cookie-name:auth_token}")
    private String cookieName;

    @Value("${jwt.refresh-cookie-name:refresh_auth_token}")
    private String refreshCookieName;
}
