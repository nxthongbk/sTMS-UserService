package com.scity.user.model.entity;

import com.scity.user.model.entity.audit.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "function_group")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FunctionGroup  extends BaseEntityAudit implements Serializable {
    private String name;
    private Integer sortNumber;
    @OneToMany(mappedBy = "functionGroup")
    @OrderBy(value = "sortNumber")
    private List<Permission> permissions;
}
