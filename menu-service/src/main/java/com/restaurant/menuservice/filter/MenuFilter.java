package com.restaurant.menuservice.filter;

import com.restaurant.data.model.IFilter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MenuFilter implements IFilter {
    private String category;
    private Boolean isAvailable;
    private String name;
    private Long id;
}
