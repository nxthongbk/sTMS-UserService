package com.scity.user.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scity.user.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = 1L;

  private final UUID id;

  private final String username;
  private final String tenant;
  @JsonIgnore
  private final String password;
  private final Collection<? extends GrantedAuthority> authorities;
  private String email;

  public UserDetailsImpl(
      UUID id,
      String username,
      String password,
      String tenant,
      Collection<? extends GrantedAuthority> authorities) {
    this.id = id;
    this.username = username;
    this.password = password;
    this.tenant = tenant;
    this.authorities = authorities;
  }

  public static UserDetailsImpl build(User user) {
    List<GrantedAuthority> authorities =
        user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    return new UserDetailsImpl(user.getId(), user.getEmail(), user.getPassword(), user.getTenantCode(), authorities);
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDetailsImpl user = (UserDetailsImpl) o;
    return Objects.equals(id, user.id);
  }

  public String getTenant() {
    return this.tenant;
  }

  public String getRole() {
    StringBuilder roles = new StringBuilder();
    for (GrantedAuthority role : this.getAuthorities()) {
      roles.append(role.getAuthority());
      roles.append("|");
    }
    if (roles.isEmpty())
      return "";
    roles.deleteCharAt(roles.length() - 1);
    return roles.toString();
  }
}
