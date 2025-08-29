package com.scity.user.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scity.user.model.entity.audit.BaseEntityAudit;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "permission")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Permission extends BaseEntityAudit implements Serializable {
    private String name;
    private String code;
    private Integer sortNumber;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "function_group_id")
    private FunctionGroup functionGroup;
}
