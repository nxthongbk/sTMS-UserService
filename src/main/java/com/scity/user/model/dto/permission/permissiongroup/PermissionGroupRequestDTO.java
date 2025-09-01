package com.scity.user.model.dto.permission.permissiongroup;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PermissionGroupRequestDTO {
    private String tenantCode;
    @NotEmpty(message = "Tên không được để trống|40013")
    private String name;
    private String description;
    @Pattern(regexp = "(ACTIVE)|(CLOSED)", message = "Trạng thái của nhóm quyền phải là ACTIVE hoặc CLOSED|40012")
    private String status;
    private List<UUID> permissionIds;
}
