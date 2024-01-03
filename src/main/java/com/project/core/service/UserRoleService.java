package com.project.core.service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    protected UserRoleService(UserRepository userRepository, RoleRepository roleRepository) {
        super(userRepository);
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public UserModel switchRole(Map<String, Object> body, Principal principal) throws AppException {
        RoleModel role = getRoleFromId((Number) body.get("roleId"));

        if (role.getRoleName() == RoleName.ROLE_ROOT) {
            throw new AppException("No permission to set a user as a Root", 418, null);
        }

        UserModel user = getUserFromPrincipal(principal);
        CompanyModel company = user.getCompany();
        UserModel userModify = getUserById((Number) body.get("userId"));

        checkPermissionAndModifyRole(user, company, userModify, role, principal);

        return updateUserInfo(userModify, principal);
    }

    public List<UserModel> switchRoles(List<Map<String, Object>> bodies, Principal principal) throws AppException {
        List<UserModel> updatedUsers = new ArrayList<>();

        for (Map<String, Object> body : bodies) {
            RoleModel role = getRoleFromId((Number) body.get("roleId"));

            if (role.getRoleName() == RoleName.ROLE_ROOT) {
                throw new AppException("No permission to set a user as a Root", 418, null);
            }

            UserModel user = getUserFromPrincipal(principal);
            CompanyModel company = user.getCompany();
            UserModel userModify = getUserById((Number) body.get("userId"));

            checkPermissionAndModifyRole(user, company, userModify, role, principal);

            updatedUsers.add(updateUserInfo(userModify, principal));
        }

        return updatedUsers;
    }


    public Object getUserInRoles(Principal principal) {
        List<RoleName> excludeRolesToShow = List.of(RoleName.ROLE_PORTAL,RoleName.ROLE_ROOT);
        UserModel user = getUserFromPrincipal(principal);
        CompanyModel companyModel = user.getCompany();
        List<RoleModel> roles = roleRepository.findAllRolesExcluding(excludeRolesToShow);

        return roles.stream()
                .map(role -> createUserRoleMap(role, companyModel))
                .collect(Collectors.toList());
    }

    public boolean isBranchInHQ(CompanyModel hq, Long branchId) {
        return hq.getBranchs().stream().anyMatch(e -> e.getId() == branchId) || hq.getId() == branchId;
    }

    private RoleModel getRoleFromId(Number roleId) throws AppException {
        return roleRepository.findById(roleId.longValue())
                .orElseThrow(() -> new AppException("Role not found", 404, null));
    }

    private UserModel getUserById(Number userId) throws AppException {
        return userRepository.findById(userId.longValue())
                .orElseThrow(() -> new AppException("User not found", 404, null));
    }

    private void checkPermissionAndModifyRole(UserModel user, CompanyModel company, UserModel userModify, RoleModel role, Principal principal) throws AppException {
        if (company.isHeadquarters() && (isBranchInHQ(company, userModify.getCompany().getId()) || userModify.getCompany().getId() == company.getId())) {
            updateUserRoles(userModify, Set.of(role), user, principal);
        } else if (!company.isHeadquarters() && company.getId() == userModify.getCompany().getId()) {
            updateUserRoles(userModify, Set.of(role), user, principal);
        } else {
            throw new AppException("No permission to change user from another company", 422, null);
        }
    }

    private void updateUserRoles(UserModel user, Set<RoleModel> roles, UserModel updater, Principal principal) throws AppException {
        user.setRoles(new HashSet<>(roles));
        user.setUpdatedBy(updater.getId());
        user.setLastModifiedDate(new Date());
        update(user.getId(), user, user, principal);
    }

    private Map<String, Object> createUserRoleMap(RoleModel role, CompanyModel companyModel) {
        Map<String, Object> userRoleMap = new LinkedHashMap<>();
        userRoleMap.put("id", role.getId());
        userRoleMap.put("name", role.getRoleName().getName());
        userRoleMap.put("users", userRepository.findByRole(role.getRoleName().toString(), companyModel.getId()).get());
        return userRoleMap;
    }

    private UserModel updateUserInfo(UserModel user, Principal principal) throws AppException {
        return update(user.getId(), user, user, principal);
    }
}
