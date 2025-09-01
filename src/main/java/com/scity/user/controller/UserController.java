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
public class UserController {
    @Autowired
    private IUserService userService;

    @PutMapping("/{id}/status")
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    @PermissionRequest(permission = "UPDATE_STAFF")
    public ResModel<UserDetailDTO> updateUserStatus(
            @RequestParam EUserStatus status,
            @PathVariable UUID id
    ) throws NotFoundException {
        return ResModel.ok(userService.updateUserStatus(id, status));
    }

    @PostMapping
    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = """
                    40005: Username only contains the characters A-Z, a-z or 0-9\n
                    40006: Username must be between 6-50 characters\n
                    40007: Password must be between 6-50 characters\n
                    40008: Invalid phone number\n
                    40009: Invalid email\n
                    40010: Username is existed"""),})
    @Hidden
    public ResModel<UserDetailDTO> createNewUser(
            @RequestBody @Valid UserCreateDTO user
    ) {
        return ResModel.ok(userService.createNewUser(user));
    }

    @GetMapping
    @Operation(summary = "Get user by page")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")})
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT", "STAFF"})
    public ResModel<PageRes<UserDetailDTO>> getAllUsers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) String tenantCode,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResModel.ok(userService.getAllUser(keyword, tenantCode, page, size, sortOrder));
    }

    @PutMapping("/{id}/password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = """
                    40007: Password must be between 6-50 characters""")})
    @AuthorizeRequest(roles = {"SYSADMIN", "TENANT"})
    @Operation(summary = "Set password")
    public ResModel<UserDetailDTO> updatePasswordUser(
            @PathVariable(value = "id") UUID id,
            @RequestBody PasswordSetDTO password
    ) throws NotFoundException {
        return ResModel.ok(userService.updatePassword(id, password.getPassword()));
    }

    @GetMapping("/{id}/info")
    public ResModel<UserBasicDTO> getUserInfo(
            @PathVariable UUID id
    ) throws NotFoundException {
        return ResModel.ok(userService.getUserInfo(id));
    }


    @GetMapping("/my-profile")
    @Operation(summary = "Get my profile")
    @AuthorizeRequest
    public ResModel<UserProfileDTO> getMyProfile() throws NotFoundException {
        return ResModel.ok(userService.getMyProfile());
    }

    @PutMapping("/my-profile")
    @Operation(summary = "Update my profile")
    @AuthorizeRequest
    public ResModel<UserProfileDTO> updateMyProfile(
            @RequestBody @Valid UserProfileRequestDTO userProfileRequest
    ) throws NotFoundException {
        return ResModel.ok(userService.updateMyProfile(userProfileRequest));
    }

    @PutMapping("/my-profile/password")
    @Operation(summary = "Update my password")
    @AuthorizeRequest
    public ResModel<String> updatePassword(
            @RequestBody @Valid PasswordDTO passwordRequest
    ) throws NotFoundException {
        userService.updatePassword(passwordRequest.getOldPassword(), passwordRequest.getNewPassword());
        return ResModel.ok("Successfully!");
    }

    @PostMapping("/forget-password")
    public ResModel<String> forgetPassword(
            @RequestBody @Valid ForgetPasswordDTO otpRequest
    ) throws NotFoundException {
        userService.sendOtp(otpRequest);
        return ResModel.ok("Successfully!");
    }

    @PostMapping("/compare-otp")
    public ResModel<String> sendOtp(
            @RequestBody @Valid OtpDTO otp
    ) throws NotFoundException {
        return ResModel.ok(userService.compareOtp(otp));
    }

    @PutMapping("/password")
    @Operation(summary = "Reset my password")
    public ResModel<String> resetPassword(
            @RequestBody @Valid ResetPasswordDTO newPassword
    ) throws NotFoundException {
        userService.resetPassword(newPassword);
        return ResModel.ok("Successfully!");
    }

    @GetMapping("/migration-user")
    @AuthorizeRequest(roles = {"SYSADMIN"})
    public ResModel<String> migrate() {
        userService.migrateUser();
        return ResModel.ok("Successfully");
    }
}