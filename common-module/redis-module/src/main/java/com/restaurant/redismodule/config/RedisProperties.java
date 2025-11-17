package com.restaurant.redismodule.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Primary
@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    
    /**
     * Redis deployment mode: STANDALONE, SENTINEL, or CLUSTER
     */
    private RedisMode mode = RedisMode.STANDALONE;
    
    /**
     * Database index used by the connection factory (only for standalone and sentinel)
     */
    private int database = 0;
    
    /**
     * Redis password
     */
    private String password;
    
    /**
     * Connection timeout
     */
    private Duration timeout = Duration.ofSeconds(60);
    
    /**
     * Standalone configuration
     */
    private Standalone standalone = new Standalone();
    
    /**
     * Sentinel configuration
     */
    private Sentinel sentinel = new Sentinel();
    
    /**
     * Cluster configuration
     */
    private Cluster cluster = new Cluster();
    
    /**
     * Lettuce pool configuration
     */
    private Pool lettuce = new Pool();
    

    
    /**
     * Redis deployment modes
     */
    public enum RedisMode {
        STANDALONE,
        SENTINEL,
        CLUSTER
    }
    
    /**
     * Standalone Redis configuration
     */
    @Setter
    @Getter
    public static class Standalone {
        private String host = "localhost";
        private int port = 6379;

    }
    
    /**
     * Sentinel Redis configuration
     */
    @Setter
    @Getter
    public static class Sentinel {
        private String master;
        private List<String> nodes;
        private String password;

    }
    
    /**
     * Cluster Redis configuration
     */

    @Setter
    @Getter
    public static class Cluster {
        private List<String> nodes;
        private int maxRedirects = 3;

    }
    
    /**
     * Pool configuration for Lettuce
     */
    @Getter
    @Setter
    public static class Pool {
        private int maxActive = 8;
        private int maxIdle = 8;
        private int minIdle = 0;
        private Duration maxWait = Duration.ofMillis(-1);
        private Duration timeBetweenEvictionRuns;
    }
}

