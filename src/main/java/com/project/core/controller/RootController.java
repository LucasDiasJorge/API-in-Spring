package com.project.core.controller;

import com.project.core.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/root")
public class RootController {

    private final CompanyService companyService;
    private final UserService userService;
    private final RootService rootService;

    public RootController(CompanyService companyService, UserService userService, RootService rootService) {
        this.companyService = companyService;
        this.userService = userService;
        this.rootService = rootService;
    }

    @PreAuthorize("hasRole('ROLE_ROOT')")
    @PutMapping("/switch-role")
    public String switchRoles(@RequestParam("id") Long id, @RequestParam("role") String role) {

        try {
            rootService.switchRoles(id, role);
            return "done";
        } catch (Exception e){
            return "fail";
        }
    }



}
