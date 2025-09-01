package com.scity.user.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {
    @Pattern(regexp = "[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}", message = "Email không hợp lệ|40009")
    @Schema(example = "useremail@gmail.com.vn")
    private String email;
    @Pattern(regexp = "(0|84)[0-9]{9,11}",
            message = "Số điện thoại không hợp lệ|40008")
    private String phone;
    @Schema(example = "Nguyen Khanh An")
    private String name;
    @Pattern(regexp = "\\w*", message = "Tên đăng nhập chỉ chứa ký tự A-Z, a-z hoặc 0-9|40005")
    @Size(min = 6, max = 50, message = "Tên đăng nhập phải có từ 6-50 ký tự|40006")
    @Schema(example = "user0026")
    private String username;
    @Schema(example = "")
    private String avatarUrl;
}
