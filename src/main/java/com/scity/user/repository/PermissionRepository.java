package com.scity.user.repository;

import com.scity.user.model.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    @Query(value = """
    select * from permission where (?1 = '00000000-0000-0000-0000-000000000000' or function_group_id = ?1) and unaccent(lower(name)) like concat('%', unaccent(lower(?2)), '%')
    """, nativeQuery = true)
    Page<Permission> getPage(UUID functionId, String keyword, Pageable pageable);

    @Query(value = """
    select * from permission where (?1 = '00000000-0000-0000-0000-000000000000' or function_group_id = ?1) and ids in ?2 and unaccent(lower(name)) like concat('%', unaccent(lower(?3)), '%')
    """, nativeQuery = true)
    Page<Permission> getPage(UUID functionId, List<UUID> ids, String keyword, Pageable pageable);
}
