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
public class TenantController {
    @Autowired
    private IUserService userService;

    @PostMapping("/tenant")
    @Operation(summary = "Create new tenant and account TENANT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = """
                    40005: Username only contains the characters A-Z, a-z or 0-9\n
                    40006: Username must be between 6-50 characters\n
                    40007: Password must be between 6-50 characters\n
                    40008: Invalid phone number\n
                    40009: Invalid email\n
                    40010: Username is existed"""),})
    @AuthorizeRequest(roles = {"SYSADMIN"})
    public ResModel<TenantDetailDTO> createNewTenant(
            @RequestBody @Valid TenantRequestDTO tenant
    ) {
        return ResModel.ok(userService.createNewTenant(tenant));
    }

    @GetMapping("/tenant")
    @Operation(summary = "Get tenant by page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    @AuthorizeRequest(roles = {"SYSADMIN"})
    public ResModel<PageRes<TenantDetailDTO>> getAllTenants(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResModel.ok(userService.getAllTenant(keyword, status, page, size));
    }

    @GetMapping("/tenant/exists")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT"})
    public ResModel<Boolean> getTenantByCode(
            @RequestParam String code
    ) {
        return ResModel.ok(userService.existsByCode(code));
    }

    @GetMapping("/tenant/{code}/detail")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    public ResModel<TenantBasicDTO> getTenantDetailByCode(
            @PathVariable(value = "code") String code
    ) {
        return ResModel.ok(userService.getTenantDetailByCode(code));
    }

    @GetMapping("/tenant/internal/{code}/detail")
    public ResModel<TenantBasicDTO> getTenantDetailByCodeInternal(
            @PathVariable(value = "code") String code
    ) {
        return ResModel.ok(userService.getTenantDetailByCode(code));
    }

    @GetMapping("/tenant/suggestion")
    @Operation(summary = "Get Tenant suggestion by page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    @AuthorizeRequest(roles = {"SYSADMIN"})
    public ResModel<PageRes<String>> getAllTenantsSuggestion(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResModel.ok(userService.getAllNameTenant(keyword, page, size, sortOrder));
    }

    @PutMapping("/tenant/{id}")
    @Operation(summary = "Update Tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = """
                    40005: Username only contains the characters A-Z, a-z or 0-9\n
                    40006: Username must be between 6-50 characters\n
                    40007: Password must be between 6-50 characters\n
                    40008: Invalid phone number\n
                    40009: Invalid email\n
                    40010: Username is existed"""),})
    @AuthorizeRequest(roles = {"SYSADMIN"})
    public ResModel<TenantDetailDTO> updateTenant(
            @PathVariable(value = "id") UUID id,
            @RequestBody @Valid TenantRequestDTO tenantUpdate
    ) throws NotFoundException {
        return ResModel.ok(userService.updateTenant(id, tenantUpdate));
    }

    @GetMapping("/tenant/{id}")
    @Operation(summary = "Get detail tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    public ResModel<TenantDetailDTO> getTenant(
            @PathVariable(value = "id") UUID id
    ) throws NotFoundException {
        return ResModel.ok(userService.getTenant(id));
    }

    @PutMapping("/tenant/{id}/status")
    @Operation(summary = "Set status of tenant and user of tenant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    @AuthorizeRequest(roles = {"SYSADMIN"})
    public ResModel<TenantDetailDTO> setStatusTenant(
            @PathVariable(value = "id") UUID id,
            @RequestParam EUserStatus status
    ) throws NotFoundException {
        return ResModel.ok(userService.updateStatus(id, status));
    }

    @PutMapping("/tenant/{id}/password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = """
                    40007: Password must be between 6-50 characters""")})
    @AuthorizeRequest(roles = {"SYSADMIN"})
    @Operation(summary = "Set password")
    public ResModel<TenantDetailDTO> updatePassword(
            @PathVariable(value = "id") UUID id,
            @RequestBody PasswordSetDTO password
    ) throws NotFoundException {
        return ResModel.ok(userService.updatePasswordTenant(id, password.getPassword()));
    }

    @GetMapping("/tenant/status/statistics")
    public ResModel<List<TenantStatusStatisticsProjection>> getTenantStatusStatistics(
    ) {
        return ResModel.ok(userService.getStatusStatistics());
    }

    @GetMapping("/tenant/status/list")
    public ResModel<PageRes<TenantStatusDTO>> getAllTenantStatus(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResModel.ok(userService.getAllTenantStatus(size, page));
    }

    @GetMapping("/tenant/internal/code/all")
    public ResModel<List<String>> getAllTenantCode() {
        return ResModel.ok(userService.getAllTenantCode());
    }

    @GetMapping("/migration-tenant")
    @AuthorizeRequest(roles = {"SYSADMIN"})
    public ResModel<String> migrate() {
        userService.migrateTenant();
        return ResModel.ok("Successfully");
    }
}