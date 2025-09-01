package com.scity.user.model.dto.contact_management;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class ContactManagementDetailDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String phone;
    private String tenantName;
    private String productPackage;
    private String note;
    private boolean isTick;
    private Date createdAt;
}
