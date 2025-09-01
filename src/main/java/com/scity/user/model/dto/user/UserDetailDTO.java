package com.scity.user.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scity.user.model.dto.location.LocationDTO;
import com.scity.user.model.dto.tenant.TenantDetailDTO;
import com.scity.user.model.entity.PermissionGroup;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserDetailDTO {
    private UUID id;
    private String email;
    private String phone;
    private String name;
    private String status;
    private String username;
    private List<String> roles;
    private String avatarUrl;
    private TenantDetailDTO tenant;
    private String tenantCode;
    private String permissionBloomFilter;
    private List<String> permissions;
    private PermissionGroup permissionGroup;
    @JsonProperty(value = "assignAllLocations", defaultValue = "false")
    private boolean assignAllLocations;
    private List<LocationDTO> locations;
}
