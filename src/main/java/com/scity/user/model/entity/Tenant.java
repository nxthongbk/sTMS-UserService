package com.scity.user.model.entity;

import com.scity.user.model.constant.EUserStatus;
import com.scity.user.model.dto.tenant.TenantRequestDTO;
import com.scity.user.model.entity.audit.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "tenant")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class Tenant extends BaseEntityAudit implements Serializable {
    private Long tenantId;
    private String avatarUrl;
    private String code;
    private String name;
    private String email;
    private String username;
    private String phone;
    private String status;

    public Tenant(String name, String email, String username, String phone) {
        this.code = username;
        this.name = name;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.status = EUserStatus.ACTIVE.name();
        Date date = new Date();
        this.setCreatedAt(date);
        this.setUpdatedAt(date);
    }

    public void update(TenantRequestDTO tenantRequest, UUID userId) {
        this.avatarUrl = tenantRequest.getAvatarUrl();
        this.name = tenantRequest.getName();
        this.email = tenantRequest.getEmail();
        this.username = tenantRequest.getUsername();
        this.phone = tenantRequest.getPhone();
        this.setUpdatedBy(userId);
        this.setUpdatedAt(new Date());
    }

    public void setNew(UUID userId) {
        this.code = username;
        this.status = EUserStatus.ACTIVE.name();
        this.setCreatedAt(new Date());
        this.setUpdatedAt(new Date());
        this.setCreatedBy(userId);
        this.setUpdatedBy(userId);
    }
}
