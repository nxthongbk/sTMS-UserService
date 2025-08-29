package com.scity.user.model.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpDTO {
    private String username;
    private String oneTimeOtp;
}
