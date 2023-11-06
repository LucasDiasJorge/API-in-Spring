package com.project.core.controller.root;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.core.dto.Response;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.CompanyModel;
import com.project.core.service.root.RootCompanyService;
import com.project.core.utils.ResponseUtil;

@RestController
@RequestMapping("/root/company")
public class RootCompanyController {

    private final RootCompanyService service;

    public RootCompanyController(RootCompanyService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<List<CompanyModel>>> findAllRoot(@RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10000") int limit,
            Principal user) throws Exception {
        List<CompanyModel> portals = service.findAllRoot(limit, offset, term);
        return ResponseUtil.createResponse(portals, 200, "200 OK", user);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> saveRoot(@Valid @RequestBody CompanyModel CompanyModel, HttpServletRequest request,
            Principal user) throws Exception {
        CompanyModel portalFind = service.saveRoot(user, CompanyModel);
        return ResponseUtil.createResponse(portalFind, 201, "201 CREATED", user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> editRoot(@PathVariable Long id, @Valid @RequestBody CompanyModel userRequest, Principal principal) throws AppException{
        CompanyModel editPortal = service.editRoot(id, userRequest, principal);
        return ResponseUtil.createResponse(editPortal, 200, "UPDATED WITH SUCCESS", principal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> deleteRoot(@PathVariable Long id, Principal principal) throws AppException{
        CompanyModel delete = service.deleteRoot(id, principal);
        return ResponseUtil.createResponse(delete, 200, "DELETED WITH SUCCESS", principal);

    }

    @PutMapping("/branch")
    @PreAuthorize("hasRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> putBranchInHq(@RequestParam Long hq,@RequestParam Long branch,Principal principal) throws AppException{
        CompanyModel companyModel = service.putBranchInHQ(hq, branch, principal);
        return ResponseUtil.createResponse(companyModel, 200, "200 OK", principal);
    }

    @DeleteMapping("/branch")
    @PreAuthorize("hasRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> removeBranchInHq(@RequestParam Long hq,@RequestParam Long branch,Principal principal) throws AppException{
        CompanyModel companyModel = service.removeBranchFromHQ(hq, branch, principal);
        return ResponseUtil.createResponse(companyModel, 200, "200 OK", principal);
    }

    
    
}
