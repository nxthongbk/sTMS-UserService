package com.scity.user.repository;

import com.scity.user.model.dto.tenant.TenantStatusStatisticsProjection;
import com.scity.user.model.entity.Tenant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findByCode(String code);

    Optional<Tenant> findByUsername(String username);

    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);

    @Query(value = """
    SELECT * FROM tenant
    WHERE
    (LOWER(unaccent(LPAD(tenant_id::::text, 4, '0'))) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    OR LOWER(unaccent(name)) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    OR LOWER(unaccent(code)) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    OR LOWER(unaccent(phone)) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    OR LOWER(unaccent(email)) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    OR LOWER(unaccent(username)) LIKE LOWER(unaccent(concat('%', ?1, '%'))))
    AND (status = ?2 or ?2 = 'ALL')
    """, nativeQuery = true)
    Page<Tenant> findAll(String keyword, String status, Pageable pageable);

    @Query(value = """
    SELECT name FROM tenant
    WHERE LOWER(unaccent(name)) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    """, nativeQuery = true)
    Page<String> findAllName(String keyword, Pageable pageable);

    @Query(value = """
    select * from tenant where code in ?1
    """, nativeQuery = true)
    List<Tenant> findAllByCode(List<String> codes);

    @Query(value = """
    select * from tenant where code = ?1
    """, nativeQuery = true)
    Tenant findTenantByCode(String code);

    long countByCode(String code);

    @Query(value = """
    SELECT status, COUNT(*) as count
    FROM tenant
    GROUP BY status;
    """, nativeQuery = true)
    List<TenantStatusStatisticsProjection> getStatusStatistics();

    @Query(value = """
    select * from tenant
    """, nativeQuery = true)
    Page<Tenant> findAllTenantStatus(Pageable pageable);

    boolean existsByCode(String code);

    @Query(value = """
    select code from tenant where status = 'ACTIVE'
    """, nativeQuery = true)
    List<String> getAllTenantCode();

    @Query(value = """
    select coalesce(max(tenant_id),0) from tenant
    """, nativeQuery = true)
    long getMaxTenantId();
}