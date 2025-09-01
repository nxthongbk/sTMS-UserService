package com.scity.user.repository;

import com.scity.user.model.entity.FunctionGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FunctionGroupRepository extends JpaRepository<FunctionGroup, UUID> {

    @Query(value = """
    select * from function_group where unaccent(lower(name)) like concat('%', unaccent(lower(?1)), '%')
    """, nativeQuery = true)
    Page<FunctionGroup> getPage(String keyword, Pageable pageable);
}
