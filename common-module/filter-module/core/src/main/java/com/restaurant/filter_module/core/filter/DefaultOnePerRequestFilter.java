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

import java.time.LocalDateTime;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
            SecurityContext securityContext = SecurityContextHolder.getOrCreateContext();
            securityContext.setRequestTime(LocalDateTime.now());
            //call DB lấy lên api đã config
            IEndpointModel iEndpointModel = iEndpointSupporter.getEndpoint(UriUtil.replaceQuery(request.getRequestURI()));
            securityContext.setEndpointModel(iEndpointModel);

            DefaultFilterRequest defaultFilterRequest = DefaultFilterRequest.builder()
                    .httpServletRequest(request)
                    .endpointModel(iEndpointModel)
                    .build();

            DefaultFilterResponse defaultFilterResponse = DefaultFilterResponse.builder()
                    .httpServletResponse(response)
                    .build();

            //duyệt qua tất cả các chain theo security type config cho api cụ thể
            iMvcFilterChainManager.filter(defaultFilterRequest, defaultFilterResponse);

            HttpServletRequest httpServletRequest = defaultFilterRequest.getHttpServletRequest();
            HttpServletResponse httpServletResponse = defaultFilterResponse.getHttpServletResponse();

            //set context để dùng trong các tầng dưới
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(httpServletRequest, httpServletResponse);

            //có thể xử lý thêm các logic log time xử lý hay gì thì tùy.
            //đoạn này là sau khi xong hết nghiệp vụ controller và trả về cho client
        } catch (Exception e) {
            //TODO handler để trả thông báo lỗi cho client theo mã lỗi văng ra
            if (e instanceof FilterException filterException) {
                //viet hàm để xử lý lỗi theo max lỗi cụ thể
            }
            //handler mã lỗi mặc định cho case k handle được lỗi cụ thể
        }
    }
}
