package com.scity.user.model.dto.user;

import com.scity.user.model.dto.location.LocationDTO;
import com.scity.user.model.entity.PermissionGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserElementDTO {
    private UUID id;
    private Long code;
    private String username;
    private String name;
    private String phone;
    private PermissionGroup permissionGroup;
    private String status;
    private String avatarUrl;
    private List<LocationDTO> locations;
    private boolean assignAllLocations;
}
