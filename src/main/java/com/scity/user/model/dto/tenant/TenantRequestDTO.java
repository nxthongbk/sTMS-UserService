package com.scity.user.model.dto.tenant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TenantRequestDTO {
    private String avatarUrl;
    @Schema(example = "Nguyen Khanh An")
    @Size(max = 255, message = "Tên khách hàng chỉ giới hạn 255 ký tự")
    private String name;
    @Pattern(regexp = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}", message = "Email không hợp lệ")
    @Schema(example = "useremail@gmail.com.vn")
    private String email;
    @Pattern(regexp = "\\w*", message = "Tên đăng nhập chỉ chứa ký tự A-Z, a-z hoặc 0-9")
    @Size(min = 4, max = 20, message = "Tên đăng nhập phải có từ 4-20 ký tự")
    @Schema(example = "user0026")
    private String username;
    @Pattern(regexp = "(0|84)[0-9]{9,11}",message = "Số điện thoại không hợp lệ")
    private String phone;
}
