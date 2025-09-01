package com.scity.user.model.dto.auth;

import com.scity.user.model.dto.user.UserDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    private UserDetailDTO userInfo;
    private TokenDTO token;
}
