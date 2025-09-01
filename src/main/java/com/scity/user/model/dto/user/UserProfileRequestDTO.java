package com.scity.user.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileRequestDTO {
    private String avatarUrl;
    @Pattern(regexp = "[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}", message = "Email không hợp lệ|40009")
    @Schema(example = "useremail@gmail.com.vn")
    private String email;
    @Pattern(regexp = "(0|84)[0-9]{9,11}",
            message = "Số điện thoại không hợp lệ|40008")
    private String phone;
}
