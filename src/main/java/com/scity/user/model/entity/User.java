package com.scity.user.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scity.user.model.constant.EDefaultValue;
import com.scity.user.model.constant.ERole;
import com.scity.user.model.constant.EUserStatus;
import com.scity.user.model.dto.user.StaffCreateDTO;
import com.scity.user.model.entity.audit.BaseEntityAudit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class User extends BaseEntityAudit implements Serializable {
  private String email;
  private String phone;
  @JsonIgnore
  private String password;
  private String name;
  private String status;
  private String username;
  private List<String> roles;
  private String avatarUrl;
  private String tenantCode;
  private String oneTimeOtp;
  @Column(columnDefinition = "integer default 0")
  private int optCount = 0;
  private Long otpExpiry;
  private String accessToken;
  private String address;
  private Long code;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name = "permission_group_id")
  private PermissionGroup permissionGroup;
  private List<UUID> locationIds;
  @Column(name = "assign_all_locations", columnDefinition = "boolean default false")
  private boolean assignAllLocations;

  public User(String username, String phone, String password, String email, String tenantCode, List<String> roles, UUID userId) {
    this.username = username;
    this.phone = phone;
    this.email = email;
    this.password = password;
    this.tenantCode = tenantCode;
    this.status = EUserStatus.ACTIVE.name();
    this.roles = roles;
    Date date = new Date();
    this.setCreatedAt(date);
    this.setUpdatedAt(date);
    this.setCreatedBy(userId);
    this.setUpdatedBy(userId);
  }

  public User(StaffCreateDTO staffCreateDTO) {
    this.tenantCode = staffCreateDTO.getTenantCode();
    this.username = staffCreateDTO.getUsername();
    this.phone = staffCreateDTO.getPhone();
    this.name = staffCreateDTO.getName();
    this.avatarUrl = staffCreateDTO.getAvatarUrl();
    this.locationIds = staffCreateDTO.getLocationIds();
  }

  public void setNew(UUID userId) {
    this.roles = List.of(ERole.SYSADMIN.name());
    this.status = EUserStatus.ACTIVE.name();
    this.tenantCode = EDefaultValue.TENANT_CODE.getValue();
    Date date = new Date();
    this.setCreatedAt(date);
    this.setUpdatedAt(date);
    this.setCreatedBy(userId);
    this.setUpdatedBy(userId);
  }

  public void update(String username, String phone, String email, List<String> roles, UUID userId) {
    this.username = username;
    this.phone = phone;
    this.email = email;
    this.roles = roles;
    this.setUpdatedAt(new Date());
    this.setUpdatedBy(userId);
  }

  public String getRoleStr() {
    StringBuilder roles = new StringBuilder();
    for (String role : this.roles) {
      roles.append(role);
      roles.append("|");
    }
    if (roles.isEmpty())
      return "";
    roles.deleteCharAt(roles.length() - 1);
    return roles.toString();
  }

}