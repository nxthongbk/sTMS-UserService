package com.scity.user.model.dto.tenant;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class TenantDetailDTO {
    private UUID id;
    private long tenantId;
    private String avatarUrl;
    private String code;
    private String name;
    private String email;
    private String username;
    private String phone;
    private String status;
    private Date createdAt;
}
