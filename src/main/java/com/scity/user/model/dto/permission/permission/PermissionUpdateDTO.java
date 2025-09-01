package com.scity.user.model.dto.permission.permission;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PermissionUpdateDTO {
    private String name;
    private UUID functionGroupId;
}
