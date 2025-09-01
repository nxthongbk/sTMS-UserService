package com.scity.user.controller;

import com.scity.user.aop.annotation.AuthorizeRequest;
import com.scity.user.aop.annotation.PermissionRequest;
import com.scity.user.exception.NotFoundException;
import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.ResModel;
import com.scity.user.model.dto.permission.permissiongroup.PermissionGroupDTO;
import com.scity.user.model.dto.permission.permissiongroup.PermissionGroupRequestDTO;
import com.scity.user.model.entity.FunctionGroup;
import com.scity.user.model.entity.Permission;
import com.scity.user.model.entity.PermissionGroup;
import com.scity.user.service.IPermissionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private IPermissionService permissionService;

    @GetMapping
    public ResModel<PageRes<Permission>> getPermissions(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) UUID permissionGroupId,
            @RequestParam(required = false) UUID functionGroupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tenantCode
            ) throws NotFoundException {
        return ResModel.ok(permissionService.getListPermission(functionGroupId, tenantCode, permissionGroupId, keyword, page, size));
    }

    @GetMapping("/function-groups")
    public ResModel<PageRes<FunctionGroup>> getFunctionGroups(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String tenantCode
    ) {
        return ResModel.ok(permissionService.getListFunctionGroup(keyword, tenantCode, page, size));
    }

    @GetMapping("/permission-groups")
    public ResModel<PageRes<PermissionGroupDTO>> getPermissionGroups(
        @RequestParam(defaultValue = "") String keyword,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String tenantCode
    ) {
        return ResModel.ok(permissionService.getListPermissionGroup(keyword, tenantCode, page, size));
    }

    @PostMapping("/permission-groups")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "CONFIGURE_PERMISSIONS")
    public ResModel<PermissionGroup> createPermissionGroup(
            @RequestBody @Valid PermissionGroupRequestDTO permissionGroupRequest
            ) {
        return ResModel.ok(permissionService.createNewPermissionGroup(permissionGroupRequest));
    }

    @PutMapping("/permission-groups/{id}")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "CONFIGURE_PERMISSIONS")
    public ResModel<PermissionGroup> updatePermissionGroup(
            @PathVariable UUID id,
            @RequestBody @Valid PermissionGroupRequestDTO permissionGroupRequest
    ) throws NotFoundException {
        return ResModel.ok(permissionService.updatePermissionGroup(id, permissionGroupRequest));
    }

    @DeleteMapping("/permission-groups/{id}")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "CONFIGURE_PERMISSIONS")
    public ResModel<String> deletePermissionGroup(
            @PathVariable UUID id
    ) throws NotFoundException {
        permissionService.deletePermissionGroup(id);
        return ResModel.ok("Successfully!");
    }

    @GetMapping("/permission-groups/{id}")
    public ResModel<PermissionGroupDTO> getPermissionGroup(
            @PathVariable UUID id
    ) throws NotFoundException {
        return ResModel.ok(permissionService.getPermissionGroup(id));
    }
}
