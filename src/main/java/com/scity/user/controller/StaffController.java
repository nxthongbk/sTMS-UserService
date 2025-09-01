package com.scity.user.controller;

import com.scity.user.aop.annotation.AuthorizeRequest;
import com.scity.user.aop.annotation.PermissionRequest;
import com.scity.user.exception.NotFoundException;
import com.scity.user.model.constant.EUserStatus;
import com.scity.user.model.dto.PageRes;
import com.scity.user.model.dto.ResModel;
import com.scity.user.model.dto.auth.*;
import com.scity.user.model.dto.tenant.*;
import com.scity.user.model.dto.user.*;
import com.scity.user.model.entity.User;
import com.scity.user.service.IUserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class StaffController {
    @Autowired
    private IUserService userService;

    @PostMapping("/staff")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "UPDATE_STAFF")
    public ResModel<UserDetailDTO> createNewStaff(
            @RequestBody @Valid StaffCreateDTO staffCreate) throws NotFoundException {
        return ResModel.ok(userService.createNewStaff(staffCreate));
    }

    @PutMapping("/staff/{id}")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "UPDATE_STAFF")
    public ResModel<UserDetailDTO> updateStaff(
            @PathVariable UUID id,
            @RequestBody @Valid StaffUpdateDTO staffUpdate) throws NotFoundException {
        return ResModel.ok(userService.updateStaff(id, staffUpdate));
    }

    @GetMapping("/staff/{id}")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "VIEW_STAFF")
    public ResModel<UserElementDTO> getStaffById(
            @PathVariable UUID id,
            @RequestParam(required = false) String tenantCode
    ) throws NotFoundException {
        return ResModel.ok(userService.getStaffDetail(id, tenantCode));
    }

    @GetMapping("/staff")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "VIEW_STAFF")
    public ResModel<PageRes<UserElementDTO>> getStaffPage(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String tenantCode,
            @RequestParam(required = false) UUID locationId,
            @RequestParam(required = false) UUID permissionGroupId,
            @RequestParam(defaultValue = "") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size ) {
        return ResModel.ok(userService.getStaffPage(keyword, tenantCode, locationId, permissionGroupId, status, page, size));
    }

    @DeleteMapping("/staff/{id}")
    @AuthorizeRequest(roles = {"TENANT", "SYSADMIN", "STAFF"})
    @PermissionRequest(permission = "UPDATE_STAFF")
    public ResModel<String> deleteStaff(
            @PathVariable UUID id
    ) throws NotFoundException {
        userService.deleteStaff(id);
        return ResModel.ok("Successfully");
    }

    @GetMapping("/staff/statistic")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    public ResModel<List<StatisticUserDTO>> getStatisticStaff(
            @RequestParam(required = false) String tenantCode
    ) {
        return ResModel.ok(userService.getUserStatistic(tenantCode));
    }

    @GetMapping("/staff/sms")
    public ResModel<List<UserElementDTO>> getUsersWithPermission(
            @RequestParam(required = false) UUID locationId,
            @RequestParam(required = false) String tenantCode
            ) {
        return ResModel.ok(userService.getUsersWithPermissionSMS(tenantCode, locationId));
    }
}