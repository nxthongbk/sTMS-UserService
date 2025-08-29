package com.scity.user.repository;

import com.scity.user.model.dto.IData;
import com.scity.user.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByUsername(String username);
  Optional<User> findByPhone(String phone);

  boolean existsByUsername(String username);

  boolean existsByPhone(String phone);

  boolean existsByName(String name);

  @Query(value = """
          select * from users where unaccent(lower(name)) like concat('%', unaccent(lower(?1)), '%') and (?2 = 'DEFAULT' or tenant_code = ?2)
          """, nativeQuery = true)
  Page<User> getPage(String keyword, String tenantCode, Pageable pageable);
  @Query(value = """
          select * from users where permission_group_id = ?1
          """, nativeQuery = true)
  List<User> findAllByPermissionGroup(UUID permissionGroupId);

  @Query(value = """
          select count(*) from users where permission_group_id = ?1
          """, nativeQuery = true)
  long countAllByPermissionGroup(UUID permissionGroupId);
  @Query(value = """
            select * from users
            where roles = '{STAFF}'
            and (
                unaccent(lower(name)) like CONCAT('%', unaccent(lower(?1)), '%')
                or unaccent(lower(username)) like CONCAT('%', unaccent(lower(?1)), '%')
                or unaccent(lower(phone)) like CONCAT('%', unaccent(lower(?1)), '%')
                or lower(unaccent(LPAD(code::::text, 4, '0'))) LIKE lower(unaccent(CONCAT('%', ?1, '%')))
            )
            and (COALESCE(?2, '') = '' or tenant_code = ?2)
            and ( assign_all_locations = true
                or (?3 = '00000000-0000-0000-0000-000000000000')
                or (assign_all_locations = false and (?3 = ANY (location_ids)))
            )
            and (status = ?4 OR ?4 = '')
            and (permission_group_id = ?5 or ?5 = '00000000-0000-0000-0000-000000000000')
            order by code, name ASC
            """, nativeQuery = true)
  Page<User> getStaffPage(String keyword, String tenantCode, UUID locationId, String status, UUID permissionGroupId, Pageable pageable);
  @Query(value = """
          select count(*) as "value", status as code from users
          where roles = '{STAFF}' and (?1 = '' or tenant_code = ?1) group by status
          """, nativeQuery = true)
  List<IData> statisticStaff(String tenantCode);

  @Query(value = """
    select coalesce(max(code),0) from users
    """, nativeQuery = true)
  long getMaxCodeUser();

  @Query(value = """
                 SELECT u.*
                   FROM users u
                   JOIN permission_group pg ON u.permission_group_id = pg.id
                   JOIN permission p ON p.id = ANY(pg.permission_ids)
                 WHERE (COALESCE(?1, '') = '' or p.code = ?1) AND u.roles = '{STAFF}' AND (COALESCE(?2, '') = '' or u.tenant_code = ?2)
                      and ( u.assign_all_locations = true
                          or (?3 = '00000000-0000-0000-0000-000000000000')
                          or (u.assign_all_locations = false and (?3 = ANY (u.location_ids)))
                      )
                 """, nativeQuery = true)
  List<User> findUsersByPermissionName(String keyword, String tenantCode, UUID locationId);
}