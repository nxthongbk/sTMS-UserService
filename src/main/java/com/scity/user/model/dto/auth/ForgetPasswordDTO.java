package com.scity.user.model.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgetPasswordDTO {
    private String username;
    private String phone;
}
