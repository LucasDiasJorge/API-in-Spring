package com.project.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.core.enums.RoleName;
import com.project.core.model.administrative.RoleModel;

public interface RoleRepository extends JpaRepository<RoleModel, Long> {

    @Query("SELECT r FROM RoleModel r WHERE r.roleName = :roleName ")
    Optional<RoleModel> findRole(@Param("roleName") RoleName roleName);

}
