package com.project.core.service.root;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.CompanyModel;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.root.RootCompanyRepository;
import com.project.core.service.AbstractService;
import com.project.core.service.UserService;

@Service
public class RootCompanyService extends AbstractService<CompanyModel>{
    
    private final RootCompanyRepository repository;
    private final UserService userService;
    
    public RootCompanyService(RootCompanyRepository repository,UserService userService) {
        super(repository);
        this.repository = repository;
        this.userService = userService;
    }

    public List<CompanyModel> findAllRoot(int limit, int offset, String term){
        Pageable pageable = PageRequest.of(offset, limit);
        if (term != null) {
            return repository.findByTermRoot(term, pageable).orElse(null);
        } else {
            return repository.findAllRoot(pageable);
        }
    }

    public CompanyModel findByIdRoot(Long id){
        return repository.findByIdRoot(id).orElse(null);
    }

    public CompanyModel saveRoot(Principal principal, CompanyModel companyRequest) throws AppException{
        UserModel user = userService.loadUserByEmail(principal.getName());
        companyRequest.setCreatedBy(user.getId());
        companyRequest.setUpdatedBy(user.getId());

        return save(companyRequest);
    }

    public CompanyModel editRoot(Long id,CompanyModel companyRequest,Principal principal) throws AppException{
        UserModel user = userService.loadUserByEmail(principal.getName());
        companyRequest.setUpdatedBy(user.getId());
        companyRequest.setLastModifiedDate(new Date());

        CompanyModel portalFind = repository.findById(id).orElseThrow();
        
        return update(id, companyRequest, portalFind, principal);
    }

    public CompanyModel deleteRoot(Long id,Principal principal) throws AppException{
        UserModel user = userService.loadUserByEmail(principal.getName());
        CompanyModel companyFind = repository.findById(id).orElseThrow();
        
        companyFind.setDeletedAt(new Date());
        companyFind.setDeletedBy(user.getId());

        return delete(id, companyFind, principal);
    }

    public CompanyModel putBranchInHQ(Long hqId, Long branchId, Principal principal) throws AppException{
        UserModel userModel = userService.loadUserByEmail(principal.getName());

        CompanyModel hq = findById(hqId);
        CompanyModel branch = findById(branchId);

        List<CompanyModel> branchs = hq.getBranchs();

        branchs.add(branch);

        hq.setBranchs(branchs);

        hq.setUpdatedBy(userModel.getId());
        hq.setLastModifiedDate(new Date());

        return save(hq);
    }

    public CompanyModel removeBranchFromHQ(Long hqId, Long branchId, Principal principal) throws AppException{
        UserModel userModel = userService.loadUserByEmail(principal.getName());


        CompanyModel hq = findById(hqId);
        CompanyModel branch = findById(branchId);

        List<CompanyModel> branchs = hq.getBranchs();

        branchs.remove(branch);

        hq.setBranchs(branchs);

        hq.setUpdatedBy(userModel.getId());
        hq.setLastModifiedDate(new Date());

        return save(hq);
    }

    

}
