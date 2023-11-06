package com.project.core.repository;

import com.project.core.model.ErrorHandlingModel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Primary
public interface ErrorHandlingRepository extends JpaRepository<ErrorHandlingModel, Long> {

    @Query("SELECT eh FROM ErrorHandlingModel eh WHERE eh.company.id = :companyId AND eh.deletedBy IS NULL")
    List<ErrorHandlingModel> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT eh FROM ErrorHandlingModel eh WHERE eh.company.id = :companyId AND eh.id = :id AND eh.deletedBy IS NULL ORDER BY eh.id")
    ErrorHandlingModel findByIdCompany(@Param("companyId") Long companyId, @Param("id") Long id);

}
