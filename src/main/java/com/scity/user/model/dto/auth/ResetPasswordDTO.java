package com.scity.user.model.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResetPasswordDTO {
    private String token;
    private String username;
    @Size(min = 8, max = 50, message = "Mật khẩu phải có từ 8-50 ký tự|40007")
    private String newPassword;
}
