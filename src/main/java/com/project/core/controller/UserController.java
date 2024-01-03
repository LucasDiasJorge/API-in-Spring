package com.project.core.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import com.project.core.exception.throwable.AppException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

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
import com.project.core.service.UserService;
import com.project.core.utils.ResponseUtil;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Response<List<UserModel>>> findAllUsers(
            @RequestParam(value = "term", required = false) String term,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10000") int limit,
            Principal user) {
        List<UserModel> users = service.findAll(limit,offset,term,user);
        return ResponseUtil.createResponse(users, 200, "200 OK");
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> save(@Valid @RequestBody UserModel userModel, HttpServletRequest request,
            Principal user) throws Exception {
        UserModel userFind = service.saveNewUser(user, userModel);
        return ResponseUtil.createResponse(userFind, 201, "201 CREATED", user);
    }

    @PostMapping("/save-root")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> saveRoot(@Valid @RequestBody UserModel userModel, HttpServletRequest request,
            Principal user) throws Exception {
        UserModel userFind = service.saveNewUserRoot(user, userModel);
        return ResponseUtil.createResponse(userFind, 201, "201 CREATED", user);
    }

    @PostMapping("/save-hq")
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> saveUserInBranch(@Valid @RequestBody UserModel userModel, HttpServletRequest request,
            Principal user) throws Exception {
        UserModel userFind = service.saveNewUserInBranch(user, userModel);
        return ResponseUtil.createResponse(userFind, 201, "201 CREATED", user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> edit(@PathVariable Long id,@RequestBody UserModel userModel, Principal user) throws AppException {
        UserModel editedUser = service.edit(id, userModel, user);
        return ResponseUtil.createResponse(editedUser, 200, "User edited with success", user);
    }

    @PutMapping("/root/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> editRoot(@PathVariable Long id,@RequestBody UserModel userModel, Principal user){
        UserModel editedUser = service.editRoot(id, userModel, user);
        return ResponseUtil.createResponse(editedUser, 200, "User edited with success", user);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN')")
    public ResponseEntity<Response<Object>> delete(@PathVariable Long id, Principal principal){
        UserModel user = service.delete(id, principal);
        return ResponseUtil.createResponse(user, 200, "deleted with success",principal);
    }

    @DeleteMapping("/root/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ROOT')")
    public ResponseEntity<Response<Object>> deleteRoot(@PathVariable Long id, Principal principal){
        UserModel user = service.deleteRoot(id, principal);
        return ResponseUtil.createResponse(user, 200, "deleted with success",principal);
    }


    @PutMapping("/change-password")
    @PreAuthorize("hasAnyRole('ROLE_ROOT','ROLE_ADMIN','ROLE_USER')")
    public ResponseEntity<Response<Object>> changePassword(@RequestBody Map<String, Object> requestBody,
            Principal user) {
        boolean success = service.changePassword(user, requestBody.get("oldPassword").toString(),
                requestBody.get("newPassword").toString());
        if (success) {
            return ResponseUtil.createResponse(null, 200, "Password changed succesfuly!");
        } else {
            return ResponseUtil.createResponse(null, 400, "Error while changing password");
        }
    }

}
