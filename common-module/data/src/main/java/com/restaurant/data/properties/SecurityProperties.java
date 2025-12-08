package com.restaurant.data.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Set;


/**
 * The type Security properties.
 */
@Setter
@Getter
@ConfigurationProperties("common.security")
public class SecurityProperties {

    @JsonProperty("cors")
    private Cors cors;

    @JsonProperty("business-endpoints")
    private Set<String> businessEndpoints;

    @JsonProperty("key")
    private Key key;

    /**
     * The type Cors.
     */
    @Setter
    @Getter
    public static class Cors {

        @JsonProperty("allowed-origins")
        private List<String> allowedOrigins;

        @JsonProperty("allowed-methods")
        private List<String> allowedMethods;

        @JsonProperty("allowed-headers")
        private List<String> allowedHeaders;


        @Override
        public String toString() {
            return "Cors{" +
                    "allowedOrigins=" + allowedOrigins +
                    ", allowedMethods=" + allowedMethods +
                    ", allowedHeaders=" + allowedHeaders +
                    '}';
        }
    }

    /**
     * The type Rate limit.
     */
    @Getter
    @Setter
    public static class RateLimit {

        @JsonProperty("limit-value")
        private Long limitValue;

        @JsonProperty("limit-time")
        private Long limitTime;
    }

    /**
     * The type Key.
     */
    @Getter
    @Setter
    public static class Key {

        @JsonProperty("secret-key")
        private String secretKey;

        @JsonProperty("private-key")
        private String privateKey;
    }
}
