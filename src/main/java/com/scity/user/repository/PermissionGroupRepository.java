package com.scity.user.repository;

import com.scity.user.model.entity.PermissionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, UUID> {

    @Query(value = """
    select * from permission_group where
    (
        unaccent(lower(name)) like concat('%', unaccent(lower(?1)), '%')
        or (lower(unaccent(LPAD(code::::text, 4, '0'))) like lower(unaccent(concat('%', ?1, '%'))))
    )
    and tenant_code = ?2
    """, nativeQuery = true)
    Page<PermissionGroup> getPage(String keyword, String tenantCode, Pageable pageable);

    @Query(value = """
    SELECT * FROM permission_group WHERE name = ?1
    """, nativeQuery = true)
    PermissionGroup findGroupByName(String group_name);

    @Query(value = """
    select coalesce(max(code),0) from permission_group
    """, nativeQuery = true)
    long maxCodePermissionGroup();
}
