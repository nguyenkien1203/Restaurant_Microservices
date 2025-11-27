package com.restaurant.menuservice.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMenuRequest {

    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Size(max = 1000, message = "Description must be less than 1000 characters")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;

    private String category;
    private String imageUrl;
    private Boolean isAvailable;

    @Min(value = 1, message = "Preparation time must be at least 1 minute")
    private Integer preparationTime;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer calories;

}
