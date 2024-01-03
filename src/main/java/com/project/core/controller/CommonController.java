package com.project.core.controller;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.core.repository.*;
import com.project.core.service.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.core.enums.RoleName;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.CompanyModel;
import com.project.core.model.administrative.RoleModel;
import com.project.core.model.administrative.UserModel;

import static com.project.core.security.EncoderConfig.getPasswordEncoder;

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;


@RestController
@RequestMapping("/core")
public class CommonController {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CommonService commonService;
    private final RequestMappingHandlerMapping handlerMapping;

    public CommonController(RequestMappingHandlerMapping handlerMapping, CommonService commonService, CompanyRepository companyRepository, UserRepository userRepository, RoleRepository roleRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.commonService = commonService;
        this.handlerMapping = handlerMapping;
    }

    @GetMapping("/ping")
    public Map<String,Object> ping(HttpServletRequest request) throws IOException {

        File info = new File("API/info.json");
        System.out.println(info.getAbsolutePath());
        ObjectMapper json = new ObjectMapper();

        return json.readValue(info,Map.class);

    }

    @GetMapping("/populate")
    public String publicRoute() throws AppException {

        CompanyService companyService = new CompanyService(companyRepository);
        UserService userService = new UserService(userRepository,null,roleRepository);

        Date now = new Date();
        Date future = new Date(999999999999L);

        CompanyModel company = new CompanyModel();
        CompanyModel branch = new CompanyModel();

        branch.setActive(true);
        branch.setCompanyName("Empresa Branch");
        branch.setCompanyPhone("(xx) xxxxxxxxx");
        branch.setCnpj("00000000001");
        branch.setClientKey("CC000COMP");
        branch.setFancyName("EMPRESA TESTE");
        branch.setManagerEmail("empresa@empresa.com");
        branch.setCreatedAt(now);
        branch.setMovingItemCreation(0);
        branch.setHeadquarters(false);

        List<CompanyModel> branchs = new ArrayList<>();
        branchs.add(branch);

        company.setActive(true);
        company.setCompanyName("Empresa Head");
        company.setCompanyPhone("(xx) xxxxxxxx");
        company.setCnpj("00000000000");
        company.setClientKey("CC000COMP");
        company.setFancyName("EMPRESA TESTE MATRIZ");
        company.setManagerEmail("matriz@empresa.com");
        company.setCreatedAt(now);
        company.setMovingItemCreation(0);
        company.setHeadquarters(true);
        company.setBranchs(branchs);

        RoleModel roleRoot = new RoleModel();
        roleRoot.setRoleName(RoleName.ROLE_ROOT);
        roleRoot.setPriority(RoleName.ROLE_ROOT.getPriority());
        roleRoot = roleRepository.save(roleRoot);

        RoleModel roleAdmin = new RoleModel();
        roleAdmin.setRoleName(RoleName.ROLE_ADMIN);
        roleAdmin.setPriority(RoleName.ROLE_ADMIN.getPriority());
        roleAdmin = roleRepository.save(roleAdmin);

        RoleModel roleUser = new RoleModel();
        roleUser.setRoleName(RoleName.ROLE_USER);
        roleUser.setPriority(RoleName.ROLE_USER.getPriority());
        roleUser = roleRepository.save(roleUser);

        RoleModel rolePortal = new RoleModel();
        rolePortal.setRoleName(RoleName.ROLE_PORTAL);
        rolePortal.setPriority(RoleName.ROLE_PORTAL.getPriority());
        rolePortal = roleRepository.save(rolePortal);

        RoleModel roleManager = new RoleModel();
        roleManager.setRoleName(RoleName.ROLE_MANAGER);
        roleManager.setPriority(RoleName.ROLE_MANAGER.getPriority());
        roleManager = roleRepository.save(roleManager);

        RoleModel roleMaintener = new RoleModel();
        roleMaintener.setRoleName(RoleName.ROLE_MAINTENER);
        roleMaintener.setPriority(RoleName.ROLE_MAINTENER.getPriority());
        roleMaintener = roleRepository.save(roleMaintener);

        RoleModel roleAuditor = new RoleModel();
        roleAuditor.setRoleName(RoleName.ROLE_AUDITOR);
        roleAuditor.setPriority(RoleName.ROLE_AUDITOR.getPriority());
        roleAuditor = roleRepository.save(roleAuditor);

        Set<RoleModel> roles = new HashSet<>();

        roles.add(roleRoot);
        roles.add(roleAdmin);
        roles.add(roleUser);
        roles.add(rolePortal);
        roles.add(roleManager);
        roles.add(roleAuditor);

        UserModel root = new UserModel();

        root.setRoles(roles.stream().filter(e -> e.getRoleName() == RoleName.ROLE_ROOT).collect(Collectors.toSet()));
        root.setEmail("ti@empresa.com");
        root.setPass(getPasswordEncoder().encode("senhadificil"));
        root.setActive(true);
        root.setCompany(company);
        root.setAttempts(0);
        root.setUsername("TI matriz");
        root.setCreatedAt(now);
        root.setLastLogin(now);
        root.setBiometry("bmhsQhU2NBMbw$kuNgZB!n!Oxo7YYcVHq!p6g#U7$k47i-1tJU1a!eyRdY-gVgFNFQM-");
        root.setCard("crhsRU0JEIYyfw7bai36NZnvt1cU8N-4YbXK0EZcCCrUzZHc2vtI?zDbaoA34MM!ESHb");

        UserModel admin = new UserModel();

        List<RoleModel> rolesAdmin = new ArrayList<>();

        rolesAdmin.add(roleAdmin);

        admin.setRoles(roles.stream().filter(e -> e.getRoleName() == RoleName.ROLE_ADMIN).collect(Collectors.toSet()));
        admin.setEmail("tifilial@empresa.com");
        admin.setPass(getPasswordEncoder().encode("senhadificil"));
        admin.setActive(true);
        admin.setCompany(branch);
        admin.setAttempts(0);
        admin.setUsername("TI Filial");
        admin.setCreatedAt(now);
        admin.setLastLogin(now);
        admin.setBiometry("bmhs944Pp4jOXA0BIYDuuitmC?s8uyKU5n$Y%Nyx7%IlRrGbTQC4tkLvkH42Y%X49O$C");
        admin.setCard("crhsblAuH%dTGw$Cbg@4#o?%W4eN!o1L2SS8v@kMgC5REDRy?ew%xOJ04k6!w8ESyxgn");

        return "done";
    }

}
