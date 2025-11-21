package com.restaurant.authservice.event;

import com.restaurant.kafkamodule.event.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TokenRefreshEvent extends BaseEvent {

    private Long id;
    private String email;
    private LocalDateTime timestamp;
    private String eventType;
}
