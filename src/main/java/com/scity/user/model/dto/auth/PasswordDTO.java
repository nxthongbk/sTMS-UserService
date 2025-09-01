package com.scity.user.model.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordDTO {
    private String oldPassword;
    @Size(min = 8, max = 50, message = "Mật khẩu phải có từ 8-50 ký tự|40007")
    private String newPassword;
}
