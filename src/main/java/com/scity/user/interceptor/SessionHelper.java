package com.scity.user.interceptor;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class SessionHelper {
    private final RequestHeaders requestHeaders;

    public UUID getCurrentUserId() {
        try {
            if (requestHeaders != null && requestHeaders.getUserId() != null)
                return requestHeaders.getUserId();
            else
                return UUID.fromString("00000000-0000-0000-0000-000000000000");
        } catch (Exception ex) {
            return UUID.fromString("11111111-1111-1111-1111-111111111111");
        }
    }
    public void setCurrentUserID(UUID userID) {
        requestHeaders.setUserId(userID);
    }
    public String getTenantId() {
        try {
            if (requestHeaders != null && requestHeaders.getTenantId() != null)
                return requestHeaders.getTenantId();
            else
                return "DEFAULT";
        } catch (Exception ex) {
            return "DEFAULT";
        }
    }

    public List<String> getRoles() {
        try {
            if (requestHeaders != null && requestHeaders.getRoles() != null) {
                return requestHeaders.getRoles();
            } else {
                return new ArrayList<>();
            }
        } catch (Exception ex) {
            return List.of("SYSTEM");
        }
    }

    public String getPermissions() {
        try {
            if (requestHeaders != null && requestHeaders.getPermissions() != null) {
                return requestHeaders.getPermissions();
            } else {
                return "";
            }
        } catch (Exception ex) {
            return "";
        }
    }

    public String getUsername() {
        try {
            if (requestHeaders != null && requestHeaders.getUsername() != null) {
                return requestHeaders.getUsername();
            } else {
                return "";
            }
        } catch (Exception ex) {
            return "";
        }
    }

    public String getLocationId() {
        try {
            if (requestHeaders != null && requestHeaders.getLocationId() != null) {
                return requestHeaders.getLocationId();
            } else {
                return null;
            }
        } catch (Exception ex) {
            return null;
        }
    }
}
