package com.project.core.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.core.enums.RoleName;
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
                userModel.setPass(encoder.encode(newPassword));
                update(userModel.getId(), userModel, findById(userModel.getId()), user);
            }
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public UserModel saveNewUserRoot(Principal principal, UserModel user) {
        try {
            Optional<UserModel> userFind = Optional.ofNullable(repository.findByEmail(user.getEmail()));

            if (!userFind.isPresent()) {
                user.setPass(encoder.encode(user.getPass()));
                List<RoleModel> roles = new ArrayList<>();
                roles.add(roleRepository.findRole(RoleName.ROLE_USER).get());
                user.setRoles(roles);
                UserModel savedUser = save(user,principal);
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
                    List<RoleModel> roles = new ArrayList<>();
                    roles.add(roleRepository.findRole(RoleName.ROLE_USER).get());
                    userRequest.setRoles(roles);
                    UserModel savedUser = save(userRequest,principal);
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

    public UserModel saveNewUser(Principal principal, UserModel userRequest) {
        try {
            CompanyModel company = loadUserByEmail(principal.getName()).getCompany();
            Optional<UserModel> userFind = Optional.ofNullable(repository.findByEmail(userRequest.getEmail()));

            if (!userFind.isPresent()) {
                userRequest.setCompany(company);
                userRequest.setPass(encoder.encode(userRequest.getPass()));
                List<RoleModel> roles = new ArrayList<>();
                roles.add(roleRepository.findRole(RoleName.ROLE_USER).get());
                userRequest.setRoles(roles);
                UserModel savedUser = save(userRequest,principal);
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

    public UserModel edit(Long id, UserModel userRequest, Principal principal) {
        try {
            UserModel user = repository.findByEmail(principal.getName());
            CompanyModel company = user.getCompany();
            Optional<UserModel> userFind = repository.findByIdNotDeleted(id);

            if (!userFind.isPresent()) {
                // Exception, user not found
                return null;
            }

            if (company.isHeadquarters()) {
                if (isBranchInHQ(company, userFind.get().getCompany().getId())) {
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setLastModifiedDate(new Date());
                    userRequest.setCompany(company);
                    if(userRequest.getPass() == null){
                        userRequest.setPass(userFind.get().getPass());
                    }else{
                        userRequest.setPass(encoder.encode(userRequest.getPass()));
                    }
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setRoles(userFind.get().getRoles());
                    return update(id, userRequest, userFind.get(), principal);
                } else {
                    return null;
                }
            } else {
                if (company.getId() == userFind.get().getCompany().getId()) {
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setLastModifiedDate(new Date());
                    userRequest.setCompany(company);
                    if(userRequest.getPass() == null){
                        userRequest.setPass(userFind.get().getPass());
                    }else{
                        userRequest.setPass(encoder.encode(userRequest.getPass()));
                    }
                    userRequest.setUpdatedBy(user.getId());
                    userRequest.setRoles(userFind.get().getRoles());
                    return update(id, userRequest, userFind.get(), principal);
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            return null;
        }

    }

    public UserModel editRoot(Long id, UserModel userRequest, Principal principal) {
        try {
            UserModel user = repository.findByEmail(principal.getName());
            Optional<UserModel> userFind = repository.findByIdNotDeleted(id);

            if (!userFind.isPresent()) {
                // Exception, user not found
                return null;
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
                // Exception, User not found;
                return null;
            }

            if (company.isHeadquarters()) {
                if (isBranchInHQ(company, userFind.get().getCompany().getId())
                        || company.getId() == userFind.get().getCompany().getId()) {
                    userFind.get().setDeletedBy(user.getId());
                    userFind.get().setDeletedAt(new Date());
                    userFind.get().setActive(false);
                    return delete(id, userFind.get(), principal);

                } else {
                    // Exception, Trying to delete user from another company
                    return null;
                }
            } else {
                if (company.getId() == userFind.get().getCompany().getId()) {
                    userFind.get().setDeletedBy(user.getId());
                    userFind.get().setDeletedAt(new Date());
                    userFind.get().setActive(false);
                    return delete(id, userFind.get(), principal);
                } else {
                    // Exception, Trying to delete user from another company
                    return null;
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

    public boolean isBranchInHQ(CompanyModel hq, Long branchId) {
        if (hq.getBranchs().stream().anyMatch(e -> e.getId() == branchId)) {
            return true;
        } else if (hq.getId() == branchId) {
            return true;
        }

        return false;
    }

}
