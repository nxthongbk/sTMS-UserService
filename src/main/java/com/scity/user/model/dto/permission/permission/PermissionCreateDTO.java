package com.scity.user.model.dto.permission.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PermissionCreateDTO {
    private String name;
    private String code;
    private UUID functionGroupId;
}
