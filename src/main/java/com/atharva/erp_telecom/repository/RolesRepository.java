package com.atharva.erp_telecom.repository;

import com.atharva.erp_telecom.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles,Long> {
    Roles findByName(String name);
}
