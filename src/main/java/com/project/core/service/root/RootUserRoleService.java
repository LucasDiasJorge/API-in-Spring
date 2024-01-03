package com.project.core.service.root;

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

    public UserModel switchRoleRoot(Map<String, Object> body, Principal principal) throws AppException {
        RoleModel role = new RoleModel();
        UserModel user = getUserFromPrincipal(principal);
        UserModel userModify = getUserById((Number) body.get("userId"));

        Set<RoleModel> roles = new HashSet<>();
        roles.add(role);
        updateUserRoles(userModify, roles, user, principal);

        return updateUserInfo(userModify, principal);
    }


    public List<UserModel> switchRolesRoot(List<Map<String, Object>> bodies, Principal principal) throws AppException {
        List<UserModel> updatedUsers = new ArrayList<>();
        RoleModel role = new RoleModel();
        UserModel user = getUserFromPrincipal(principal);

        for (Map<String, Object> body : bodies) {
            UserModel userModify = getUserById((Number) body.get("userId"));
            Set<RoleModel> roles = new HashSet<>();
            roles.add(role);
            updateUserRoles(userModify, roles, user, principal);
            updatedUsers.add(updateUserInfo(userModify, principal));
        }

        return updatedUsers;
    }

    public Object getUserInRoles(Principal principal) {
        List<RoleName> allowedRolesToShow = List.of(RoleName.ROLE_USER, RoleName.ROLE_MANAGER, RoleName.ROLE_ADMIN);
        UserModel user = getUserFromPrincipal(principal);
        CompanyModel companyModel = user.getCompany();
        List<RoleModel> roles = roleRepository.findAll();

        return roles.stream()
                .filter(role -> allowedRolesToShow.contains(role.getRoleName()))
                .map(role -> createUserRoleMap(role, companyModel))
                .collect(Collectors.toList());
    }

    public boolean isBranchInHQ(CompanyModel hq, Long branchId) {
        return hq.getBranchs().stream().anyMatch(e -> e.getId() == branchId) || hq.getId() == branchId;
    }

    private UserModel getUserById(Number userId) throws AppException {
        return userRepository.findById(userId.longValue())
                .orElseThrow(() -> new AppException("User not found", 404, null));
    }


    private void updateUserRoles(UserModel user, Set<RoleModel> roles, UserModel updater, Principal principal)
            throws AppException {
        user.setRoles(new HashSet<>(roles));
        user.setUpdatedBy(updater.getId());
        user.setLastModifiedDate(new Date());
        update(user.getId(), user, user, principal);
    }

    private Map<String, Object> createUserRoleMap(RoleModel role, CompanyModel companyModel) {
        Map<String, Object> userRoleMap = new LinkedHashMap<>();
        userRoleMap.put("id", role.getId());
        userRoleMap.put("name", role.getRoleName().getName());
        userRoleMap.put("users", userRepository.findByRole(role.toString(), companyModel.getId()).get());
        return userRoleMap;
    }

    private UserModel updateUserInfo(UserModel user, Principal principal) throws AppException {
        return update(user.getId(), user, user, principal);
    }


}
