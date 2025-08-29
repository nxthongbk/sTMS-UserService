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
public class StaffUpdateDTO {
    @Pattern(regexp = "(0|84)[0-9]{9,11}",
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
