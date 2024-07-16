package com.project.core.repository;

import com.project.core.model.integration.IntegrationModel;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Primary
public interface IntegrationModelRepository extends JpaRepository</*...*/, Long> {

    @Query("SELECT i FROM IntegrationModel i WHERE i.company.id = :companyId AND i.id = :id AND i.deletedBy IS NULL ORDER BY i.id DESC")
    IntegrationModel findByIdCompany(@Param("companyId") Long companyId, @Param("id") Long id);

    @Query("SELECT i FROM IntegrationModel i WHERE i.company.id = :companyId AND i.uriTrigger = :uri AND i.methodTrigger = :method AND i.deletedBy IS NULL ORDER BY i.id DESC")
    IntegrationModel findByTriggers(@Param("companyId") Long companyId, @Param("uri") String uri, @Param("method") String method);

}
