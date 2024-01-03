package com.project.core.service;

import java.security.Principal;
import java.util.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.core.enums.RoleName;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.CompanyModel;
import com.project.core.model.administrative.RoleModel;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.RoleRepository;
import com.project.core.repository.UserRepository;

@Service
public class UserService extends AbstractService<UserModel> {

    private UserRepository repository;
    private PasswordEncoder encoder;
    private RoleRepository roleRepository;

    public UserService(UserRepository repository, PasswordEncoder encoder, RoleRepository roleRepository) {
        super(repository);
        this.repository = repository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    public List<UserModel> findAll(int limit, int offset, String term, Principal principal) {
        CompanyModel company = loadUserByEmail(principal.getName()).getCompany();
        if (company.isHeadquarters()) {
            Pageable pageable = PageRequest.of(offset, limit);
            List<Long> companiesId = new ArrayList<>();
            companiesId.addAll(company.getBranchs().stream().map(CompanyModel::getId).toList());
            companiesId.add(company.getId());
            if (term != null) {
                return repository.findByTermHeadquarters(companiesId, term, pageable).orElse(null);
            } else {
                return repository.findByCompanyIdHeadquarters(companiesId, pageable).orElse(null);
            }
        } else {
            Pageable pageable = PageRequest.of(offset, limit);
            if (term != null) {
                return repository.findByTerm(company.getId(), term, pageable).orElse(null);
            } else {
                return repository.findByCompanyId(company.getId(), pageable).orElse(null);
            }
        }
    }

    public UserModel loadUserByEmail(String email) throws UsernameNotFoundException {

        UserModel user = repository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User [" + email + "] not found");
        }

        return user;
    }

    public boolean changePassword(Principal user, String password, String newPassword) {
        try {
            UserModel userModel = loadUserByEmail(user.getName());
            if (encoder.matches(password, userModel.getPass())) {
                if (newPassword.length() < 6) {
                    throw new AppException("New Password must have length equals or greater then six", 422, null);
                }
                userModel.setPass(encoder.encode(newPassword));
                update(userModel.getId(), userModel, findById(userModel.getId()), user);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public Map<String, Object> changeUsername(String newUsername, Principal principal) {
        try {
            UserModel userModel = getUserFromPrincipal(principal);
            if (newUsername.length() < 1 || newUsername == null) {
                throw new AppException("New Username can be empty", 422, null);
            }
            if(repository.findByUsernameAndCompanyId(newUsername,userModel.getCompany().getId()).isPresent() && !newUsername.equals(userModel.getUsername())){
                throw new AppException("Username already in use",422,null);
            }

            userModel.setUsername(newUsername);
            update(userModel.getId(), userModel, findById(userModel.getId()), principal);
            return Map.of("message", "username was change successfully");

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return Map.of("message", "Failed to change username: " + e.getMessage());
        }

    }

    public UserModel saveNewUserRoot(Principal principal, UserModel user) {
        try {
            Optional<UserModel> userFind = Optional.ofNullable(repository.findByEmail(user.getEmail()));

            if (!userFind.isPresent()) {
                user.setPass(encoder.encode(user.getPass()));
                Set<RoleModel> roles = new HashSet<>();
                roles.add(roleRepository.findRole(RoleName.ROLE_USER).get());
                user.setRoles(roles);
                UserModel savedUser = save(user, principal);
                return savedUser;
            } else {
                // Exception, User already exist, with the given email, for the given company
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public UserModel saveNewUserInBranch(Principal principal, UserModel userRequest) {
        try {
            Optional<UserModel> userFind = Optional.ofNullable(repository.findByEmail(userRequest.getEmail()));

            if (userFind.isPresent()) {
                // Exception, User already exist, with the given email
                return null;
            }

            CompanyModel userCompany = loadUserByEmail(principal.getName()).getCompany();
            CompanyModel branchCompany = userRequest.getCompany();

            if (userCompany.isHeadquarters()) {
                if (isBranchInHQ(userCompany, branchCompany.getId())) {
                    userRequest.setCompany(branchCompany);
                    userRequest.setPass(encoder.encode(userRequest.getPass()));
                    Set<RoleModel> roles = new HashSet<>();
                    roles.add(roleRepository.findRole(RoleName.ROLE_USER).get());
                    userRequest.setRoles(roles);
                    UserModel savedUser = save(userRequest, principal);
                    return savedUser;
                } else {
                    // Exception, branch is not inside hq company and can't save user to this
                    // company(branch)
                    return null;
                }
            } else {
                // Exception, company is not headquarter and can't save user to another company
                return null;
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public UserModel saveNewUser(Principal principal, UserModel userRequest) throws AppException {
        try {
            CompanyModel company = loadUserByEmail(principal.getName()).getCompany();
            Optional<UserModel> userFind = Optional.ofNullable(repository.findByEmail(userRequest.getEmail()));

            if (!userFind.isPresent()) {
                userRequest.setCompany(company);
                userRequest.setPass(encoder.encode(userRequest.getPass()));
                Set<RoleModel> roles = new HashSet<>();
                roles.add(roleRepository.findRole(RoleName.ROLE_USER).get());
                userRequest.setRoles(roles);
                UserModel savedUser = save(userRequest, principal);
                return savedUser;
            } else {
                throw new AppException("Email " + userRequest.getEmail() + " already exists!",422,null);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new AppException("Error creating new User: " + e.getMessage(),422,null);
        }
    }

    public UserModel edit(Long id, UserModel userRequest, Principal principal) throws AppException {
        try {
            UserModel user = repository.findByEmail(principal.getName());
            CompanyModel company = user.getCompany();
            Optional<UserModel> userFind = repository.findByIdNotDeleted(id);

            if (!userFind.isPresent()) {
                throw new AppException("User was not found",422,null);
            }

            if (company.isHeadquarters()) {
                if (isBranchInHQ(company, userFind.get().getCompany().getId())) {
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setLastModifiedDate(new Date());
                    userRequest.setCompany(company);
                    if (userRequest.getPass() == null) {
                        userRequest.setPass(userFind.get().getPass());
                    } else {
                        userRequest.setPass(encoder.encode(userRequest.getPass()));
                    }
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setRoles(userFind.get().getRoles());
                    return update(id, userRequest, userFind.get(), principal);
                } else {
                    throw new AppException("Cannot update user from other company",422,null);
                }
            } else {
                if (company.getId() == userFind.get().getCompany().getId()) {
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setLastModifiedDate(new Date());
                    userRequest.setCompany(company);
                    if (userRequest.getPass() == null) {
                        userRequest.setPass(userFind.get().getPass());
                    } else {
                        userRequest.setPass(encoder.encode(userRequest.getPass()));
                    }
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setRoles(userFind.get().getRoles());
                    return update(id, userRequest, userFind.get(), principal);
                } else {
                    throw new AppException("Cannot update user from other company",422,null);
                }
            }

        } catch (Exception e) {
            throw new AppException("Error while editing user: " + e.getMessage(),422,null);
        }

    }

    public UserModel editRoot(Long id, UserModel userRequest, Principal principal) {
        try {
            UserModel user = repository.findByEmail(principal.getName());
            Optional<UserModel> userFind = repository.findByIdNotDeleted(id);

            if (!userFind.isPresent()) {
                throw new AppException("User was not found",422,null);
            }

            userRequest.setUpdatedBy(user.getId());
            userRequest.setLastModifiedDate(new Date());
            return update(id, userRequest, userFind.get(), principal);

        } catch (Exception e) {
            return null;
        }
    }

    public UserModel delete(Long id, Principal principal) {
        try {

            UserModel user = repository.findByEmail(principal.getName());
            CompanyModel company = user.getCompany();
            Optional<UserModel> userFind = repository.findByIdNotDeleted(id);

            if (!userFind.isPresent()) {
                throw new AppException("User was not found",422,null);
            }

            if (company.isHeadquarters()) {
                if (isBranchInHQ(company, userFind.get().getCompany().getId())
                        || company.getId() == userFind.get().getCompany().getId()) {
                    userFind.get().setDeletedBy(user.getId());
                    userFind.get().setDeletedAt(new Date());
                    userFind.get().setActive(false);
                    userFind.get().setRoles(null);
                    return delete(id, userFind.get(), principal);

                } else {
                    throw new AppException("Cannot delete user from another company!",422,null);
                }
            } else {
                if (company.getId() == userFind.get().getCompany().getId()) {
                    userFind.get().setDeletedBy(user.getId());
                    userFind.get().setDeletedAt(new Date());
                    userFind.get().setActive(false);
                    userFind.get().setRoles(null);
                    return delete(id, userFind.get(), principal);
                } else {
                    throw new AppException("Cannot delete user from another company!",422,null);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public UserModel deleteRoot(Long id, Principal principal) {
        try {
            UserModel user = repository.findByEmail(principal.getName());
            Optional<UserModel> userFind = repository.findByIdNotDeleted(id);

            if (!userFind.isPresent()) {
                // Exception, User not found;
                return null;
            }
            userFind.get().setDeletedBy(user.getId());
            userFind.get().setDeletedAt(new Date());
            userFind.get().setActive(false);
            return delete(id, userFind.get(), principal);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Map<String,Object>> getSimpleUser(Principal principal){
        UserModel user = getUserFromPrincipal(principal);
        CompanyModel companyModel = user.getCompany();
        List<UserModel> users = repository.findByCompanyId(companyModel.getId(), Pageable.unpaged()).get();
        List<Map<String,Object>> ret = new ArrayList<>();

        for (UserModel u : users) {
            if(!u.getRoles().stream().anyMatch(e -> e.getRoleName() == RoleName.ROLE_ROOT || e.getRoleName() == RoleName.ROLE_PORTAL)){
                Map<String,Object> map = new HashMap<>();
                map.put("id",u.getId());
                map.put("username", u.getUsername());
                map.put("email",u.getEmail());
                CompanyModel userCompany = u.getCompany();
                map.put("company", Map.of
                        (
                                "id",userCompany.getId(),
                                "fancyName",userCompany.getFancyName(),
                                "companyName", userCompany.getCompanyName(),
                                "managerEmail", userCompany.getManagerEmail()
                        )
                );
                ret.add(map);
            }
        }

        return ret;

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
