package com.restaurant.authservice.filter;

import com.restaurant.data.model.IFilter;
import lombok.Builder;
import lombok.Data;

//TODO chuản đúng format không khai báo lung tung tên package
//nên tạo các package v class mang tính gợi ý đọc là biết nó lm gì k nên để tên 1 kiểu logic 1 kiểu
@Data
@Builder
public class SessionFilter implements IFilter {

    private String authId;      // Session ID

    private Long userId;        // Find sessions by user

    private String userEmail;   // Find sessions by email

    private Boolean isActive;   // Filter by active status

    private Boolean includeRevoked;  // Include revoked sessions in results
}
