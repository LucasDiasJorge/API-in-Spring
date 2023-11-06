package com.project.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.core.model.administrative.CompanyModel;

@Primary
public interface CompanyRepository extends JpaRepository<CompanyModel, Long> {

    @Query("SELECT c FROM CompanyModel c WHERE c.id = :companyId AND c.deletedBy IS NULL ")
    List<CompanyModel> findByCompanyId(@Param("companyId") Long companyId);

    @Query("SELECT c FROM CompanyModel c WHERE c.id = :companyId AND c.deletedBy IS NULL ")
    CompanyModel findByIdCompany(@Param("companyId") Long companyId);

    @Query("SELECT c FROM CompanyModel c WHERE (c.fancyName = :name OR c.companyName = :name) AND c.deletedBy IS NULL")
    Optional<CompanyModel> findByFancyNameOrName(@Param("name") String name);

    @Query("SELECT c FROM CompanyModel c WHERE (:company MEMBER OF c.branchs OR c = :company) AND c.id = :id AND c.deletedBy IS NULL")
    Optional<CompanyModel> findBranchInHeadquarter(@Param("id") Long id, @Param("company") CompanyModel company);


}
