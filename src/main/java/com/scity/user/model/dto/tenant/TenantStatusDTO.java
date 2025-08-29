package com.scity.user.model.dto.tenant;

import lombok.Data;

import java.util.UUID;

@Data
public class TenantStatusDTO {
    private UUID id;
    private String code;
    private String name;
    private String avatarUrl;
    private String status;
}
