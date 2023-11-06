package com.project.core.controller.root;

import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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
import com.project.core.model.administrative.UserModel;
import com.project.core.service.root.RootUserService;
import com.project.core.utils.ResponseUtil;

@RestController
@RequestMapping("/root/user")
public class RootUserController {

    private final RootUserService service;

    public RootUserController(RootUserService service) {
        this.service = service;
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<List<UserModel>>> findAllRoot(@RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10000") int limit,
            Principal user) throws Exception {
        List<UserModel> users = service.findAllRoot(limit, offset, term, user);
        return ResponseUtil.createResponse(users, limit, term, user);
    }

    @GetMapping("/company/{companyId}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<List<UserModel>>> findAllByCompanyIdRoot(@RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10000") int limit,
            @PathVariable Long companyId,
            Principal user) throws Exception {
        List<UserModel> users = service.findAllByCompanyIdRoot(companyId,limit, offset, term);
        return ResponseUtil.createResponse(users, limit, term, user);
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> saveRoot(@RequestBody UserModel userModel, HttpServletRequest request,
            Principal user) throws Exception {
        UserModel userFind = service.saveNewUserRoot(user, userModel);
        return ResponseUtil.createResponse(userFind, 201, "201 CREATED", user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> editRoot(@PathVariable Long id, @RequestBody UserModel userRequest, Principal principal){
        UserModel editUser = service.editRoot(id, userRequest, principal);
        return ResponseUtil.createResponse(editUser, 200, "UPDATED WITH SUCCESS", principal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> deleteRoot(@PathVariable Long id, Principal principal){
        UserModel delete = service.deleteRoot(id, principal);
        return ResponseUtil.createResponse(delete, 200, "DELETED WITH SUCCES", principal);

    }




}
