package com.scity.user.model.entity.audit;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Data
@NoArgsConstructor
@MappedSuperclass
@JsonIgnoreProperties(
        value = {"createdAt", "updatedAt", "createdBy", "updatedBy"}, allowGetters = true)
public abstract class BaseEntityAudit extends BaseEntity implements Serializable {
  @JsonIgnore
  private UUID createdBy;

  @JsonIgnore
  private UUID updatedBy;

  @JsonIgnore
  private Date createdAt;
  @JsonIgnore
  private Date updatedAt;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BaseEntityAudit)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    BaseEntityAudit that = (BaseEntityAudit) o;
    return createdBy.equals(that.createdBy)
        && updatedBy.equals(that.updatedBy)
        && createdAt.equals(that.createdAt)
        && updatedAt.equals(that.updatedAt);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), createdBy, updatedBy, createdAt, updatedAt);
  }

  @Override
  public String toString() {
    return "BaseEntityAudit{"
        + "created_by='"
        + createdBy
        + "'"
        + ", updated_by='"
        + updatedBy
        + "'"
        + ", created_at='"
        + createdAt
        + "'"
        + ", updated_at='"
        + updatedAt
        + "'"
        + "}"
        + super.toString();
  }
}
