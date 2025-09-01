package com.scity.user.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scity.user.model.dto.permission.permissiongroup.PermissionGroupRequestDTO;
import com.scity.user.model.entity.audit.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "permission_group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionGroup extends BaseEntityAudit implements Serializable {
    private String name;
    @JsonIgnore
    private String tenantCode;
    private String description;
    private String status;
    private List<UUID> permissionIds;
    private long code;

    @JsonIgnore
    @OneToMany(mappedBy = "permissionGroup")
    private List<User> users;

    public PermissionGroup(PermissionGroupRequestDTO permissionGroupRequest, UUID userId) {
        this.name = permissionGroupRequest.getName();
        this.description = permissionGroupRequest.getDescription();
        this.status = permissionGroupRequest.getStatus();
        this.permissionIds = permissionGroupRequest.getPermissionIds();
        this.setCreatedAt(new Date());
        this.setCreatedBy(userId);
        this.setUpdatedAt(new Date());
        this.setUpdatedBy(userId);
    }

    public void update(PermissionGroupRequestDTO permissionGroupRequest, UUID userId) {
        this.name = permissionGroupRequest.getName();
        this.description = permissionGroupRequest.getDescription();
        this.status = permissionGroupRequest.getStatus();
        this.permissionIds = permissionGroupRequest.getPermissionIds();
        this.setUpdatedAt(new Date());
        this.setUpdatedBy(userId);
    }
}
