package com.scity.user.model.dto.permission.permissiongroup;

import com.scity.user.model.entity.PermissionGroup;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PermissionGroupDTO {
    private UUID id;
    private String name;
    private String tenantCode;
    private String description;
    private String status;
    private List<UUID> permissionIds;
    private long code;
    private int userCount;

    // Constructors, getters, and setters
    public PermissionGroupDTO(PermissionGroup permissionGroup, int userCount) {
        this.id = permissionGroup.getId();
        this.name = permissionGroup.getName();
        this.tenantCode = permissionGroup.getTenantCode();
        this.description = permissionGroup.getDescription();
        this.status = permissionGroup.getStatus();
        this.permissionIds = permissionGroup.getPermissionIds();
        this.code = permissionGroup.getCode();
        this.userCount = userCount;
    }
}
