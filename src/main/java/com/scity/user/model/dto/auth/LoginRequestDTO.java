package com.scity.user.model.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @Pattern(regexp = "\\w*", message = "Tên đăng nhập chỉ chứa ký tự A-Z, a-z hoặc 0-9|40005")
    @Size(min = 4, max = 20, message = "Tên đăng nhập phải có từ 4-20 ký tự|40006")
    @Schema(example = "user0026")
    private String username;
    @Size(min = 8, max = 50, message = "Mật khẩu phải có từ 8-50 ký tự|40007")
    private String password;
}
