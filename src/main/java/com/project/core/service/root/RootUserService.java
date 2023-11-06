package com.project.core.service.root;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.project.core.enums.RoleName;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.administrative.RoleModel;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.RoleRepository;
import com.project.core.repository.root.RootUserRepository;
import com.project.core.service.AbstractService;

@Service
public class RootUserService extends AbstractService<UserModel> {

    private RootUserRepository repository;
    private PasswordEncoder encoder;
    private RoleRepository roleRepository;

    public RootUserService(RootUserRepository repository, PasswordEncoder encoder, RoleRepository roleRepository) {
        super(repository);
        this.repository = repository;
        this.encoder = encoder;
        this.roleRepository = roleRepository;
    }

    public List<UserModel> findAllRoot(int limit, int offset, String term, Principal principal) {
        Pageable pageable = PageRequest.of(offset, limit);
        if (term != null) {
            return repository.findByTermRoot(term, pageable).orElse(null);
        } else {
            return repository.findAllRoot(pageable);
        }
    }

    public List<UserModel> findAllByCompanyIdRoot(Long companyId, int limit, int offset, String term){
        Pageable pageable = PageRequest.of(offset, limit);
        return repository.findByCompanyId(companyId, pageable).orElse(new ArrayList<UserModel>());
    }

    public UserModel findByidRoot(Long id) throws AppException{
        return super.findById(id);
    }

    public UserModel saveNewUserRoot(Principal principal, UserModel user) {
        try {
            Optional<UserModel> userFind = Optional.ofNullable(repository.findByEmail(user.getEmail()));

            if (!userFind.isPresent()) {
                user.setPass(encoder.encode(user.getPass()));
                List<RoleModel> roles = new ArrayList<>();
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

}
