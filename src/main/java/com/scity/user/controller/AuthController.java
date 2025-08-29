package com.scity.user.controller;

import com.scity.user.model.dto.ResModel;
import com.scity.user.model.dto.auth.LoginDTO;
import com.scity.user.model.dto.auth.LoginRequestDTO;
import com.scity.user.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Login")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully"),
        @ApiResponse(responseCode = "400", description = """
                40001: Incorrect username or password\n
                40002: Account has been bocked\n
                40003: Account's tenant has been bocked\n
                40005: Username only contains the characters A-Z, a-z or 0-9\n
                40006: Username must be between 6-50 characters\n
                40007: Password must be between 6-50 characters""")})
    public ResModel<LoginDTO> login(
            @RequestBody @Valid LoginRequestDTO loginRequest
    ) {
        return ResModel.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully"),
            @ApiResponse(responseCode = "400", description = """
                    40004: Incorrect refreshToken""")})
    public ResModel<LoginDTO> refresh(
            @RequestParam String refreshToken
    ) {
        return ResModel.ok(authService.refreshToken(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully")
    })
    public ResModel<String> logout(
            @RequestParam String refreshToken
    ) {
        authService.logout(refreshToken);
        return ResModel.ok("Successfully");
    }
}
