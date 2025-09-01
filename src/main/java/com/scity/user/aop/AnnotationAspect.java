package com.scity.user.aop;

import com.scity.user.aop.annotation.AuthorizeRequest;
import com.scity.user.aop.annotation.PermissionRequest;
import com.scity.user.exception.AuthenticationException;
import com.scity.user.exception.ForbiddenException;
import com.scity.user.interceptor.SessionHelper;
import com.scity.user.model.constant.ERole;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Slf4j
@Aspect
@Component
public class AnnotationAspect {
    @Autowired
    SessionHelper sessionHelper;

    @Around("@annotation(authorizationRequest)")
    public Object checkAuthorizePermission(ProceedingJoinPoint joinPoint, AuthorizeRequest authorizationRequest) throws Throwable {

        if (sessionHelper.getCurrentUserId() != null && !sessionHelper.getCurrentUserId().toString().equals("00000000-0000-0000-0000-000000000000")) {
            String[] roles = authorizationRequest.roles();
            List<String> userRoles = sessionHelper.getRoles();
            if (roles.length == 0 || userRoles.contains(ERole.SYSTEM.name())) {
                return joinPoint.proceed();
            }
            for (String role : roles) {
                if (userRoles.contains(role))
                    return joinPoint.proceed();
            }
            throw new ForbiddenException("No permission");
        }

        throw new AuthenticationException("Authenticate is requested!");
    }

    @Around("@annotation(permissionRequest)")
    public Object checkAuthorizePermission(ProceedingJoinPoint joinPoint, PermissionRequest permissionRequest) throws Throwable {
        if (sessionHelper.getCurrentUserId() != null && !sessionHelper.getCurrentUserId().toString().equals("00000000-0000-0000-0000-000000000000")) {
            String[] permissions = permissionRequest.permission();
            List<String> userRoles = sessionHelper.getRoles();
            if (permissions.length == 0
                    || userRoles.contains(ERole.SYSTEM.name())
                    || userRoles.contains(ERole.SYSADMIN.name())
                    || userRoles.contains(ERole.TENANT.name())
                    || userRoles.contains(ERole.MANAGER.name())) {
                return joinPoint.proceed();
            }
            String userPermissions = sessionHelper.getPermissions();
            if (userPermissions.isEmpty())
                throw new ForbiddenException("No permission");

            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(userPermissions));
            BloomFilter<String> bloomFilter;
            try {
                bloomFilter = BloomFilter.readFrom(inputStream, Funnels.unencodedCharsFunnel());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            for (String permission : permissions) {
                if (bloomFilter.mightContain(permission))
                    return joinPoint.proceed();
            }
            throw new ForbiddenException("No permission");
        }
        throw new AuthenticationException("Authenticate is requested!");
    }
}