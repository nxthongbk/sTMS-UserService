package com.scity.user.interceptor;


import com.scity.user.model.constant.EDefaultValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class RestTemplateRequestInterceptor implements ClientHttpRequestInterceptor  {
    private final UUID userId;

    public RestTemplateRequestInterceptor(UUID _userId) {
        userId = _userId;
    }

    public RestTemplateRequestInterceptor() {
        userId = null;
    }
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        try{
            String currentUserId = "";
            try{
                currentUserId =
                        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                                .getRequest()
                                .getHeader("userId");
            }catch (Exception e){
                currentUserId = userId.toString();
            }

            String tenantId = null;
            try{
                tenantId = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()
                        .getHeader("X-Tenant-ID");
            } catch (Exception e){
                tenantId = EDefaultValue.TENANT_CODE.getValue();
            }

            String roleStr = "";
            try {
                roleStr = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()
                        .getHeader("roles");
            } catch (Exception e) {
            }

            String permissions = "";
            try {
                permissions = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()
                        .getHeader("permissions");
            } catch (Exception e) {
            }

            String staitonId = "";
            try {
                staitonId = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()
                        .getHeader("location");
            } catch (Exception e) {
            }

            String username = "";
            try {
                username = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest()
                        .getHeader("username");
            } catch (Exception e) {
            }

            if (currentUserId != null && !currentUserId.isEmpty()) {
                request.getHeaders().set("userId", currentUserId);
            }
            if (tenantId != null && !tenantId.isEmpty()) {
                request.getHeaders().set("X-Tenant-ID", tenantId);
            }
            if (roleStr != null && !roleStr.isEmpty()) {
                request.getHeaders().set("roles", roleStr);
            }
            if (permissions!= null && !permissions.isEmpty()) {
                request.getHeaders().set("permissions", permissions);
            }
            if (staitonId!= null && !staitonId.isEmpty()) {
                request.getHeaders().set("location", staitonId);
            }
            if (username!= null && !username.isEmpty()) {
                request.getHeaders().set("username", username);
            }

        } catch (Exception ex){
            request.getHeaders().set("userId", "11111111-1111-1111-1111-111111111111");
            request.getHeaders().set("X-Tenant-ID", EDefaultValue.TENANT_CODE.getValue());
            request.getHeaders().set("roles", "");
            request.getHeaders().set("permisstions", "");
            request.getHeaders().set("location", "");
            request.getHeaders().set("username", "");
        }

        return execution.execute(request, body);
    }
}
