package com.restaurant.filter_module.core.filter;

import com.restaurant.data.model.IEndpointModel;
import com.restaurant.data.properties.SecurityProperties;
import com.restaurant.filter_module.core.chain.IMvcFilterChainManager;
import com.restaurant.filter_module.core.context.SecurityContext;
import com.restaurant.filter_module.core.context.SecurityContextHolder;
import com.restaurant.filter_module.core.endpoint.IEndpointSupporter;
import com.restaurant.filter_module.core.exception.FilterException;
import com.restaurant.filter_module.core.model.DefaultFilterRequest;
import com.restaurant.filter_module.core.model.DefaultFilterResponse;
import com.restaurant.filter_module.core.util.UriUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * The type Default one per request filter.
 */
@Slf4j
public class DefaultOnePerRequestFilter extends BaseOnePerRequestFilter {
    private final IEndpointSupporter iEndpointSupporter;
    private final IMvcFilterChainManager iMvcFilterChainManager;

    /**
     * Instantiates a new Base vnpay one per business request filter.
     *
     * @param securityProperties the security properties
     */
    public DefaultOnePerRequestFilter(SecurityProperties securityProperties,
                                      IEndpointSupporter iEndpointSupporter,
                                      IMvcFilterChainManager iMvcFilterChainManager) {
        super(securityProperties);
        this.iEndpointSupporter = iEndpointSupporter;
        this.iMvcFilterChainManager = iMvcFilterChainManager;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) {
        try {
            //cache rp để xử lý edit thêm nếu cần. ví dụ mã hóa rp trước khi trả cho client
            ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
            SecurityContext securityContext = SecurityContextHolder.getOrCreateContext();
            securityContext.setRequestTime(LocalDateTime.now());
            //call DB lấy lên api đã config
            IEndpointModel iEndpointModel = iEndpointSupporter.getEndpoint(UriUtil.replaceQuery(request.getRequestURI()), request.getMethod());
            securityContext.setEndpointModel(iEndpointModel);

            DefaultFilterRequest defaultFilterRequest = DefaultFilterRequest.builder()
                    .httpServletRequest(request)
                    .endpointModel(iEndpointModel)
                    .build();

            DefaultFilterResponse defaultFilterResponse = DefaultFilterResponse.builder()
                    .httpServletResponse(responseWrapper)
                    .build();

            //duyệt qua tất cả các chain theo security type config cho api cụ thể
            iMvcFilterChainManager.filter(defaultFilterRequest, defaultFilterResponse);

            HttpServletRequest httpServletRequest = defaultFilterRequest.getHttpServletRequest();
            HttpServletResponse httpServletResponse = defaultFilterResponse.getHttpServletResponse();

            //set context để dùng trong các tầng dưới
            SecurityContextHolder.setContext(securityContext);

            filterChain.doFilter(httpServletRequest, httpServletResponse);

            //xử lý formater ở chain cuối dk set ví dụ như jwt response filter hay rsa response filter
            if (defaultFilterResponse.getFormatter() != null) {
                defaultFilterResponse.getFormatter().format(httpServletResponse);
            }

            //copy toàn bộ rp từ các lớp xử lý để trả cho client
            responseWrapper.copyBodyToResponse();
        } catch (Exception e) {
            handleFilterError(response, e);
            log.error("Filter error for {}: {}", request.getRequestURI(), e.getMessage());
        }
    }

    private void handleFilterError(HttpServletResponse response, Exception e) {
        try {
            int statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            String errorMessage = "Internal server error";

            if (e instanceof FilterException filterException) {
                statusCode = filterException.getHttpStatusCode();
                errorMessage = filterException.getMessage();
            }

            // Set appropriate status based on error type
            if (statusCode == 0 || statusCode == 500) {
                // Default to 401 for authentication errors
                if (e.getMessage() != null &&
                        (e.getMessage().contains("Authentication") ||
                                e.getMessage().contains("JWT") ||
                                e.getMessage().contains("token"))) {
                    statusCode = HttpStatus.UNAUTHORIZED.value();
                }
                if (e.getMessage() != null && e.getMessage().contains("Access denied")) {
                    statusCode = HttpStatus.FORBIDDEN.value();
                }
            }

            response.setStatus(statusCode);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    String.format("{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d}",
                            HttpStatus.valueOf(statusCode).getReasonPhrase(),
                            errorMessage,
                            statusCode)
            );
            response.getWriter().flush();
        } catch (IOException ioException) {
            log.error("Failed to write error response", ioException);
        }
    }
}
