package com.scity.user.service;

import com.scity.user.exception.NotFoundException;
import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.permission.permissiongroup.PermissionGroupDTO;
import com.scity.user.model.dto.permission.permissiongroup.PermissionGroupRequestDTO;
import com.scity.user.model.entity.FunctionGroup;
import com.scity.user.model.entity.Permission;
import com.scity.user.model.entity.PermissionGroup;

import java.util.UUID;

public interface IPermissionService {

    PageRes<Permission> getListPermission(UUID functionGroupId, String tenantCode, UUID permissionGroupId, String keyword, int page, int size) throws NotFoundException;

    PageRes<FunctionGroup> getListFunctionGroup(String keyword, String tenantCode, int page, int size);

    PageRes<PermissionGroupDTO> getListPermissionGroup(String keyword, String tenantCode, int page, int size);
    PermissionGroup createNewPermissionGroup(PermissionGroupRequestDTO permissionGroupRequest);
    PermissionGroup updatePermissionGroup(UUID id, PermissionGroupRequestDTO permissionGroupRequest) throws NotFoundException;
    void deletePermissionGroup(UUID id) throws NotFoundException;
    PermissionGroupDTO getPermissionGroup(UUID id) throws NotFoundException;
}
