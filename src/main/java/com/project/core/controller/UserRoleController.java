package com.project.core.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.core.dto.Response;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.UserModel;
import com.project.core.service.UserRoleService;
import com.project.core.utils.ResponseUtil;

@RestController
@RequestMapping("/user-role")
public class UserRoleController {

    private final UserRoleService service;

    public UserRoleController(UserRoleService service) {
        this.service = service;
    }

    @PutMapping("/switch/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> switchRoles(@RequestBody Map<String,Object> role, @PathVariable Long userId, Principal user) throws AppException{
        UserModel userModel = service.switchRole(userId, role.get("role").toString(), user);
        return ResponseUtil.createResponse(userModel, 200, "Role switched succesfully",user);
    }

    @PutMapping("/root/switch/{userId}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> switchRolesRoot(@RequestBody Map<String,Object> role, @PathVariable Long userId, Principal user) throws AppException{
        UserModel userModel = service.switchRole(userId, role.get("role").toString(), user);
        return ResponseUtil.createResponse(userModel, 200, "Role switched succesfully",user);
    }

    @GetMapping()
    public ResponseEntity<?> findAll(Principal user){
        return ResponseUtil.createResponse(service.getUserInRoles(user), 200, "200 OK", user);
    }



    
    
}
