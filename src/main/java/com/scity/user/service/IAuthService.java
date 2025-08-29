package com.scity.user.service;

import com.scity.user.model.dto.auth.LoginDTO;
import com.scity.user.model.dto.auth.LoginRequestDTO;

public interface IAuthService {
    LoginDTO login(LoginRequestDTO loginRequest);

    void logout(String refreshToken);

    LoginDTO refreshToken(String refreshToken);

    boolean comparePasswords(String rawPassword, String encodedPassword);

    void setGatewayFilter(String key);
}
