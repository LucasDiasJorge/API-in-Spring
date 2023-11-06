package com.project.core.service;


import com.project.core.model.administrative.CompanyModel;
import com.project.core.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CompanyService extends AbstractService<CompanyModel> {

    private CompanyRepository repository;
    public CompanyService(CompanyRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public List<CompanyModel> findByCompanyId(Long companyId) {
        return repository.findByCompanyId(companyId);
    }

    public CompanyModel findByIdCompany(Long companyId) {
        return repository.findByIdCompany(companyId);
    }
}
