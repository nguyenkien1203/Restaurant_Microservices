package com.restaurant.filter_module.core.entity;

import com.restaurant.data.model.IEndpointModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "endpoint_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointEntity implements IEndpointModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "endpoint", unique = true, nullable = false)
    private String endpoint;

    @Column(name = "path_pattern", nullable = false)
    private String pathPattern;

    @Column(name = "channel")
    private String channel;

    @Column(name = "description")
    private String description;

    @Column(name = "security_type")
    private String securityType;

    @Column(name = "rate_limit")
    private Long rateLimit;

    @Column(name = "limit_time")
    private Long limitTime;

    @Column(name = "is_public")
    @Builder.Default
    private boolean isPublic = false;

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @Override
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public String getChannel() {
        return channel;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getSecurityType() {
        return securityType;
    }

    @Override
    public Long getRateLimit() {
        return rateLimit;
    }

    @Override
    public Long getLimitTime() {
        return limitTime;
    }

    @Override
    public boolean isPublic() {
        return isPublic;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }
}

