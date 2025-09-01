package com.scity.user.interceptor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class RequestHeaders {
    private UUID userId;
    private String tenantId;
    private List<String> roles;
    private String permissions;
    private String locationId;
    private String username;
}
