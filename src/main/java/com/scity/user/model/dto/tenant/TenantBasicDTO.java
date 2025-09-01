package com.scity.user.model.dto.tenant;

import lombok.Data;

import java.util.UUID;

@Data
public class TenantBasicDTO {
    private UUID id;
    private String code;
    private String avatarUrl;
    private String name;
    private String phone;
}
