package com.scity.user.model.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class StaffCreateDTO {
    private String tenantCode;
    @Pattern(regexp = "\\w*", message = "Tên đăng nhập chỉ chứa ký tự A-Z, a-z hoặc 0-9")
    @Size(min = 4, max = 20, message = "Tên đăng nhập phải có từ 4-20 ký tự")
    @Schema(example = "user0026")
    private String username;
    @Pattern(regexp = "(0|\\+84)[0-9]{9}",
            message = "Số điện thoại không hợp lệ")
    private String phone;
    @Schema(example = "Nguyen Khanh An")
    @Size(max = 255, message = "Tên nhân sự chỉ giới hạn 255 ký tự")
    private String name;
    @Schema(example = "")
    private String avatarUrl;
    private List<UUID> locationIds;
    @NotNull(message = "Chưa chọn nhóm quyền")
    private UUID permissionGroupId;
    @JsonProperty(value = "assignAllLocations", defaultValue = "false")
    private boolean assignAllLocations;
}
