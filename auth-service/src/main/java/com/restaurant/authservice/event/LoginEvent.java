package com.restaurant.authservice.event;

import com.restaurant.kafkamodule.event.BaseEvent;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LoginEvent extends BaseEvent {

    private Long id;

    private String email;

    private String role;

    private Boolean isActive;
}
