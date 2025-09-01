package com.scity.user.model.dto.contact_management;

import lombok.Data;

@Data
public class ContactManagementDTO {
    private String fullName;
    private String email;
    private String phone;
    private String tenantName;
    private String productPackage;
    private String note;
    private boolean isTick;
}
