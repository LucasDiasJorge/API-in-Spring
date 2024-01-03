package com.project.core.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
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

    @SuppressWarnings({"unchecked"})
    @PutMapping("/switch")
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<?> switchRoles(@RequestBody Object body, Principal user) throws AppException {
        if (body instanceof HashMap) {
            UserModel userModel = service.switchRole((Map<String, Object>) body, user);
            return ResponseUtil.createResponse(userModel, 200, "Role switched successfully", user);
        } else if (body instanceof List) {
            List<UserModel> userModels = service.switchRoles((List<Map<String, Object>>) body, user);
            return ResponseUtil.createResponse(userModels, 200, "Roles switched successfully", user);
        }

        return ResponseEntity.badRequest().body("Invalid request body type");
    }

    @GetMapping()
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<?> findAll(Principal user){
        return ResponseUtil.createResponse(service.getUserInRoles(user), 200, "200 OK", user);
    }

}
