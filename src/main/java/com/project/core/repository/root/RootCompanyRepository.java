package com.project.core.repository.root;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.core.model.administrative.CompanyModel;
import com.project.core.repository.CompanyRepository;

public interface RootCompanyRepository extends CompanyRepository{
    
    @Query("SELECT c FROM CompanyModel c WHERE c.id = :companyId AND c.deletedBy IS NULL ")
    Optional<CompanyModel> findByIdRoot(@Param("companyId") Long companyId);

    @Query("SELECT c FROM CompanyModel c WHERE c.deletedBy IS NULL")
    List<CompanyModel> findAllRoot(Pageable pageable);

    @Query("SELECT p FROM CompanyModel p WHERE p.deletedBy IS NULL AND (LOWER(p.companyName) LIKE LOWER(CONCAT('%',:term,'%')) OR LOWER(p.fancyName) LIKE LOWER(CONCAT('%',:term,'%')) OR LOWER(p.cnpj) LIKE LOWER(CONCAT('%',:term,'%'))OR LOWER(p.managerName) LIKE LOWER(CONCAT('%',:term,'%'))) ORDER BY p.id DESC")
    Optional<List<CompanyModel>> findByTermRoot(@Param("term") String term, Pageable pageable);

    @Query("SELECT c FROM CompanyModel c WHERE c.id = :id AND c.deletedBy IS NULL")
    Optional<CompanyModel> findByIdNotDeleted(@Param("id") Long id);
}
