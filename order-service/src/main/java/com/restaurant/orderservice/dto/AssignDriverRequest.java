package com.restaurant.orderservice.dto;

import com.restaurant.data.model.IBaseModel;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignDriverRequest implements IBaseModel<Long> {

    @NotNull(message = "Driver ID is required")
    private Long driverId;


    @Override
    public Long getId() {
        return driverId;
    }
}

