package com.scity.user.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
public class RequesterHeaderInterceptor implements HandlerInterceptor {
  private final RequestHeaders requesterHeaders;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String userId = request.getHeader("userId");
    if (userId != null) requesterHeaders.setUserId(UUID.fromString(userId));
    String tenantId = request.getHeader("X-Tenant-ID");
    if (tenantId != null) requesterHeaders.setTenantId(tenantId);
    String roleStr = request.getHeader("roles");
    if (roleStr != null) {
      List<String> roles = Arrays.stream(roleStr.split("\\|")).toList();
      requesterHeaders.setRoles(roles);
    }
    String permissions = request.getHeader("permissions");
    if (permissions != null) {
      requesterHeaders.setPermissions(permissions);
    }
    String location = request.getHeader("location");
    if (location!=null && !location.isEmpty()) {
      requesterHeaders.setLocationId(location);
    }
    String username = request.getHeader("username");
    if (username != null) {
      requesterHeaders.setUsername(username);
    }
    return true;
  }
}
