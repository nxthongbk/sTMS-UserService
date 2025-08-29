package com.scity.user.repository;

import com.scity.user.model.entity.Tenant;
import com.scity.user.model.entity.ContactManagement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ContactManagementRepository extends JpaRepository<ContactManagement, UUID> {

    @Query(value = """
    SELECT * FROM contact_management
    WHERE LOWER(unaccent(full_name)) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    OR LOWER(unaccent(full_name)) LIKE LOWER(unaccent(concat('%', ?1, '%')))
    OR unaccent(lower(phone)) like concat('%', unaccent(lower(?1)), '%')
    OR unaccent(lower(email)) like concat('%', unaccent(lower(?1)), '%')
    OR unaccent(lower(tenant_name)) like concat('%', unaccent(lower(?1)), '%')
    """, nativeQuery = true)
    Page<ContactManagement> findAll(String keyword, Pageable pageable);

}
