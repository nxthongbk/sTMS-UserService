package com.scity.user.model.dto.user;

import com.scity.user.model.dto.tenant.TenantBasicDTO;
import com.scity.user.model.dto.location.LocationDTO;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserProfileDTO {
    private UUID id;
    private String email;
    private String phone;
    private String name;
    private String status;
    private String username;
    private List<String> roles;
    private String avatarUrl;
    private TenantBasicDTO tenant;
    private LocationDTO location = null;
}
