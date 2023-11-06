package com.project.core.service.root;

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
import com.project.core.service.AbstractService;

@Service
public class RootUserRoleService extends AbstractService<UserModel> {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    protected RootUserRoleService(UserRepository userRepository, RoleRepository roleRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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


}
