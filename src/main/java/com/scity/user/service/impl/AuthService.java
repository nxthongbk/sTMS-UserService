package com.scity.user.service.impl;

import com.scity.user.exception.BadRequestException;
import com.scity.user.model.constant.EDefaultValue;
import com.scity.user.model.constant.EUserStatus;
import com.scity.user.model.dto.auth.LoginDTO;
import com.scity.user.model.dto.auth.LoginRequestDTO;
import com.scity.user.model.dto.auth.TokenDTO;
import com.scity.user.model.dto.tenant.TenantDetailDTO;
import com.scity.user.model.dto.user.UserDetailDTO;
import com.scity.user.model.entity.Tenant;
import com.scity.user.model.entity.Permission;
import com.scity.user.model.entity.User;
import com.scity.user.repository.TenantRepository;
import com.scity.user.repository.PermissionRepository;
import com.scity.user.repository.UserRepository;
import com.scity.user.security.jwt.JwtUtils;
import com.scity.user.service.IAuthService;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;

@Service
public class AuthService implements IAuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TenantRepository TenantRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private RedisTemplate<String, Serializable> redisTemplate;
    @Value("${scity.app.jwtRefresh}")
    private String keyAuthRefresh;
    @Value("${bloom-filter-permission}")
    private int bloomFilterTotal;
    @Value("${scity.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Override
    public LoginDTO login(LoginRequestDTO loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
             .orElseThrow(() -> new BadRequestException("Sai tên đăng nhập hoặc mật khẩu", 40001));

        if (!comparePasswords(loginRequest.getPassword(), user.getPassword()))
            throw new BadRequestException("Sai tên đăng nhập hoặc mật khẩu", 40001);

        if (user.getStatus().equals(EUserStatus.BLOCKED.name()))
            throw new BadRequestException("Tài khoản đã bị khóa", 40002);

        if (!user.getTenantCode().equals(EDefaultValue.TENANT_CODE.getValue())) {
            Tenant tenant = TenantRepository.findByCode(user.getTenantCode())
                    .orElseThrow(() -> new BadRequestException("Địa điểm đã bị khóa", 40003));
            if (tenant.getStatus().equals(EUserStatus.BLOCKED.name()))
                throw new BadRequestException("Địa điểm đã bị khóa", 40003);
        }

        UserDetailDTO userResponse = getUserDetail(user);
        String location = user.isAssignAllLocations() ? "ALL" : user.getLocationIds() == null ? "" : user.getLocationIds().toString();
        String accessToken = jwtUtils.generateTokenFromUsername(user.getUsername(), user.getId(), user.getTenantCode(), user.getRoleStr(), userResponse.getPermissionBloomFilter(), location , user.getPermissionGroup() == null ? "" : String.valueOf(user.getPermissionGroup().getId()));

        String refreshToken = jwtUtils.generateRefreshTokenFromUsername(user.getUsername(), user.getId());

        TokenDTO tokens = new TokenDTO();
        tokens.setAccessToken(accessToken);
        tokens.setRefreshToken(refreshToken);
        redisTemplate.opsForValue().set(keyAuthRefresh + refreshToken, user.getId().toString(), Duration.ofSeconds((jwtUtils.getTokenExpiryFromJWT(refreshToken).getTime() - new Date().getTime())/1000));
        return new LoginDTO(userResponse, tokens);
    }

    @Override
    public void logout(String refreshToken) {
        try {
            redisTemplate.delete(keyAuthRefresh + refreshToken);
        } catch (Exception ignore){}
    }

    @Override
    public LoginDTO refreshToken(String oldRefreshToken) {
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(keyAuthRefresh + oldRefreshToken))) {
            throw new BadRequestException("Refresh token sai", 40004);
        }
        String username  = jwtUtils.getUsernameFromJwtToken(oldRefreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("Refresh token sai", 40004));
        if (user.getStatus().equals(EUserStatus.BLOCKED.name()))
            throw new BadRequestException("Tài khoản đã bị khóa", 40002);

        if (!user.getTenantCode().equals(EDefaultValue.TENANT_CODE.getValue())) {
            Tenant tenant = TenantRepository.findByCode(user.getTenantCode())
                    .orElseThrow(() -> new BadRequestException("Địa điểm đã bị khóa", 40003));
            if (tenant.getStatus().equals(EUserStatus.BLOCKED.name()))
                throw new BadRequestException("Địa điểm đã bị khóa", 40003);
        }
        UserDetailDTO userResponse = getUserDetail(user);
        String location = user.isAssignAllLocations() ? "ALL" : user.getLocationIds() == null ? "" : user.getLocationIds().toString();
        String accessToken = jwtUtils.generateTokenFromUsername(user.getUsername(), user.getId(), user.getTenantCode(), user.getRoleStr(), userResponse.getPermissionBloomFilter(), location, user.getPermissionGroup() == null ? "" : String.valueOf(user.getPermissionGroup().getId()));
        String refreshToken = jwtUtils.generateRefreshTokenFromUsername(user.getUsername(), user.getId(), jwtUtils.getTokenExpiryFromJWT(oldRefreshToken));

        TokenDTO tokens = new TokenDTO();
        tokens.setAccessToken(accessToken);
        tokens.setRefreshToken(refreshToken);
        try {
            redisTemplate.delete(keyAuthRefresh + oldRefreshToken);
        } catch (Exception ignore){}
        redisTemplate.opsForValue().set(keyAuthRefresh + refreshToken, user.getId().toString(), Duration.ofSeconds((jwtUtils.getTokenExpiryFromJWT(refreshToken).getTime() - new Date().getTime())/1000));
        return new LoginDTO(userResponse, tokens);
    }


    @Override
    public boolean comparePasswords(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public UserDetailDTO getUserDetail(User user) {
        UserDetailDTO userDetail = modelMapper.map(user, UserDetailDTO.class);
        List<Permission> permissions;
        if (user.getPermissionGroup() != null && user.getPermissionGroup().getPermissionIds() != null &&
                !user.getPermissionGroup().getPermissionIds().isEmpty()) {
            permissions = permissionRepository.findAllById(user.getPermissionGroup().getPermissionIds());
        } else {
            permissions = new ArrayList<>();
        }
        userDetail.setPermissionBloomFilter(getPermission(permissions));
        userDetail.setPermissions(permissions.stream().map(Permission::getCode).toList());
        if (user.getTenantCode() != null) {
            Tenant tenant = TenantRepository.findByCode(user.getTenantCode())
                    .orElse(null);
            if (tenant != null) {
                userDetail.setTenant(modelMapper.map(tenant, TenantDetailDTO.class));
            }
        }
        return userDetail;
    }

    private String getPermission(List<Permission> permissions) {
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.unencodedCharsFunnel(), bloomFilterTotal, 0.01);
        for (Permission permission : permissions) {
            bloomFilter.put(permission.getCode());
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            bloomFilter.writeTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    @Override
    public void setGatewayFilter(String key) {
        redisTemplate.opsForValue().set("GATE_FITER::" + key, "", Duration.ofMillis(jwtExpirationMs));
    }
}
