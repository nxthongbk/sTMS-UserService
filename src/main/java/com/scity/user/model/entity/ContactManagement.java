package com.scity.user.model.entity;


import com.scity.user.model.dto.contact_management.ContactManagementDTO;
import com.scity.user.model.entity.audit.BaseEntityAudit;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "contact_management")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ContactManagement extends BaseEntityAudit implements Serializable {
    private String fullName;
    private String email;
    private String phone;
    private String tenantName;
    private String productPackage;
    private String note;
    @Column(name = "is_tick")
    private boolean isTick;

    public ContactManagement(ContactManagementDTO contactManagementDTO ) {
        this.fullName = contactManagementDTO.getFullName();
        this.email = contactManagementDTO.getEmail();
        this.phone =contactManagementDTO.getPhone();
        this.tenantName = contactManagementDTO.getTenantName();
        this.productPackage = contactManagementDTO.getProductPackage();
        this.note = contactManagementDTO.getNote();
        this.isTick = contactManagementDTO.isTick();
        this.setCreatedAt(new Date());
        this.setUpdatedAt(new Date());
    }
    public void update(ContactManagementDTO contactManagementDTO ) {
        this.fullName = contactManagementDTO.getFullName();
        this.email = contactManagementDTO.getEmail();
        this.phone =contactManagementDTO.getPhone();
        this.tenantName = contactManagementDTO.getTenantName();
        this.productPackage = contactManagementDTO.getProductPackage();
        this.note = contactManagementDTO.getNote();
        this.isTick = contactManagementDTO.isTick();
        this.setUpdatedAt(new Date());
    }
}
