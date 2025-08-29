package com.scity.user.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserBasicDTO {
    private UUID id;
    private String name;
    private String phone;
    private String username;
    private String avatarUrl;
}
