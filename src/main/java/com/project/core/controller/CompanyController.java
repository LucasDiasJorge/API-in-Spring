package com.project.core.controller;

import java.security.Principal;
import java.util.*;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.core.dto.Response;
import com.project.core.enums.RoleName;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.CompanyModel;
import com.project.core.model.administrative.RoleModel;
import com.project.core.model.administrative.UserModel;
import com.project.core.service.CompanyService;
import com.project.core.service.UserService;
import com.project.core.utils.ResponseUtil;

@RestController
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;
    private final UserService userService;
    private PasswordEncoder encoder;


    @Autowired
    public CompanyController(CompanyService companyService, UserService userService, PasswordEncoder encoder) {
        this.companyService = companyService;
        this.userService = userService;
        this.encoder = encoder;
    }

    @PreAuthorize("hasRole('ROLE_ROOT')")
    @GetMapping
    public ResponseEntity<Response<List<CompanyModel>>> findAll(Principal user) throws AppException {

        List<CompanyModel> companies = companyService.findAll();

        return ResponseUtil.createResponse(companies, 200, "200 OK", user);
    }

    @PreAuthorize("hasRole('ROLE_ROOT')")
    @GetMapping("/{id}")
    public ResponseEntity<Response<CompanyModel>> findById(@PathVariable Long id, Principal user) {

        CompanyModel company = companyService.findByIdCompany(id);

        return ResponseUtil.createResponse(company, 200, "200 OK", user);
    }

    @PreAuthorize("hasRole('ROLE_ROOT')")
    @PostMapping
    public ResponseEntity<Response<CompanyModel>> save(@RequestBody CompanyModel company, Principal user) throws AppException {

        UserModel userModel = userService.loadUserByEmail(user.getName());

        company.setCreatedBy(userModel.getId());
        company.setUpdatedBy(userModel.getId());
        company.setActive(true);

        CompanyModel savedCompany = companyService.save(company);

        UserModel companyManager = new UserModel();
        companyManager.setCompany(company);
        companyManager.setEmail(company.getManagerEmail());
        companyManager.setAttempts(0);
        companyManager.setLastLogin(new Date(0L));
        companyManager.setPass(encoder.encode("initialpass"));
        companyManager.setUsername(company.getManagerName());
        companyManager.setActive(true);

        RoleModel role = new RoleModel();
        role.setRoleName(RoleName.ROLE_ADMIN);
        Set<RoleModel> roleModelList = new HashSet<>();
        roleModelList.add(role);
        companyManager.setRoles(roleModelList);

        userService.save(companyManager);

        return ResponseUtil.createResponse(savedCompany, 200, "200 OK",user);
    }

    @PreAuthorize("hasRole('ROLE_ROOT')")
    @PutMapping("/{id}")
    public ResponseEntity<Response<CompanyModel>> update(@PathVariable Long id, @RequestBody CompanyModel company, Principal user) throws AppException{

        UserModel userModel = userService.loadUserByEmail(user.getName());

        Date now = new Date();

        CompanyModel companyVerify = new CompanyModel();

        try{
            companyVerify = companyService.findByIdCompany(id);
            company.setLastModifiedDate(now);
            company.setUpdatedBy(userModel.getId());
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }

        CompanyModel updatedCompany = companyService.update(id, company, companyVerify,user);
        return ResponseUtil.createResponse(updatedCompany, 200, "200 OK", user);
    }

    @PreAuthorize("hasRole('ROLE_ROOT')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<CompanyModel>> delete(@PathVariable Long id, Principal user) throws AppException {

        UserModel userModel = userService.loadUserByEmail(user.getName());

        Date now = new Date();

        CompanyModel companyVerify = new CompanyModel();

        try{
            companyVerify = companyService.findByIdCompany(id);
            companyVerify.setLastModifiedDate(now);
            companyVerify.setUpdatedBy(userModel.getId());
            companyVerify.setDeletedAt(now);
            companyVerify.setDeletedBy(userModel.getId());
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }

        CompanyModel deletedCompany = companyService.delete(id, companyVerify,user);

        return ResponseUtil.createResponse(deletedCompany, 200, "200 OK", user);
    }

    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN','ROLE_MANAGER')")
    @GetMapping("/branchs")
    public ResponseEntity<Response<Object>> findBranchInCompany(Principal principal) throws AppException{
        UserModel user = userService.loadUserByEmail(principal.getName());
        CompanyModel companyModel = user.getCompany();
        Map<String,Object> company = new HashMap<>();
        company.put("id", companyModel.getId());
        company.put("companyName",companyModel.getCompanyName());
        company.put("fancyName", companyModel.getFancyName());
        company.put("managerEmail", companyModel.getManagerEmail());
        Map<String,Object> ret = new LinkedHashMap<>();
        ret.put("headquarter", company);
        ret.put("branchs", companyModel.getBranchs());


        if(companyModel.isHeadquarters()){
            return ResponseUtil.createResponse(ret, 200, "200 OK",principal);
        }else{
            throw new AppException("Empresa não é matriz", 422, null);
        }
    }

}
