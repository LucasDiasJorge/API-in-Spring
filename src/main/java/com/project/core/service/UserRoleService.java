package com.project.core.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.core.enums.RoleName;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.CompanyModel;
import com.project.core.model.administrative.RoleModel;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.RoleRepository;
import com.project.core.repository.UserRepository;

@Service
public class UserRoleService extends AbstractService<UserModel> {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    protected UserRoleService(UserRepository userRepository, RoleRepository roleRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserModel switchRole(Long userId, String roleName, Principal principal) throws AppException {
        if (roleName.equals("root") || roleName.equals("ROOT")) {
            // Not authorized to set a user as root;
            return null;
        }

        UserModel user = userRepository.findByEmail(principal.getName());
        CompanyModel company = user.getCompany();
        Optional<UserModel> userFind = userRepository.findById(userId);

        if (!userFind.isPresent()) {
            // Execption, user not found;
            return null;
        }

        UserModel userModify = userFind.get();

        RoleModel role = roleRepository.findRole(RoleName.valueOf("ROLE_" + roleName.toUpperCase())).orElseThrow();

        if (company.isHeadquarters()) {
            if (isBranchInHQ(company, userFind.get().getCompany().getId())
                    || userModify.getCompany().getId() == company.getId()) {

                List<RoleModel> roles = new ArrayList<>();
                roles.add(role);
                userModify.getRoles().clear();
                userModify.setRoles(roles);
                userModify.setUpdatedBy(user.getId());
                userModify.setLastModifiedDate(new Date());
                return update(userId, userModify, userFind.get(), principal);

            }
        } else {
            if (company.getId() == userModify.getCompany().getId()) {
                List<RoleModel> roles = new ArrayList<>();
                roles.add(role);
                userModify.getRoles().clear();
                userModify.setRoles(roles);
                userModify.setUpdatedBy(user.getId());
                userModify.setLastModifiedDate(new Date());
                return update(userId, userModify, userFind.get(), principal);

            } else {
                // Exception, trying to edit user frm another company;
                return null;
            }
        }

        return null;
    }

    public UserModel switchRoleRoot(Long userId, String roleName, Principal principal) throws AppException {
        UserModel user = userRepository.findByEmail(principal.getName());
        Optional<UserModel> userFind = userRepository.findById(userId);

        if (!userFind.isPresent()) {
            // Execption, user not found;
            return null;
        }

        UserModel userModify = userFind.get();

        RoleModel role = roleRepository.findRole(RoleName.valueOf("ROLE_" + roleName.toUpperCase())).orElseThrow();
        List<RoleModel> roles = new ArrayList<>();
        roles.add(role);
        userModify.getRoles().clear();
        userModify.setRoles(roles);
        userModify.setUpdatedBy(user.getId());
        userModify.setLastModifiedDate(new Date());
        return update(userId, userModify, userFind.get(), principal);
    }

    public Object getUserInRoles(Principal principal) {
        CompanyModel companyModel = userRepository.findByEmail(principal.getName()).getCompany();
        List<Map<String, Object>> l = new ArrayList<>();
        for (RoleName e : RoleName.values()) {
            if (e.toString() != "ROLE_PORTAL") {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("roleName", e.toString().replace("ROLE_", ""));
                m.put("users", userRepository.findByRole(e.toString(), companyModel.getId()).get());
                l.add(m);
            }
        }
        return l;
    }

    public boolean isBranchInHQ(CompanyModel hq, Long branchId) {
        if (hq.getBranchs().stream().anyMatch(e -> e.getId() == branchId)) {
            return true;
        } else if (hq.getId() == branchId) {
            return true;
        }

        return false;
    }

}
