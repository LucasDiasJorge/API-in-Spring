package com.project.core.security.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.project.core.model.administrative.UserModel;
import com.project.core.repository.UserRepository;
import com.project.core.security.data.UserDetailsData;
import com.project.core.service.AbstractService;

@Component
@Service
public class UserDetailsSeviceImpl extends AbstractService<UserModel> implements UserDetailsService {

    private UserRepository userRepository;

    protected UserDetailsSeviceImpl(UserRepository repository) {
        super(repository);
        this.userRepository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserModel user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User [" + email + "] not found");
        }
        return new UserDetailsData(user);
    }

    public UserModel loadUserByEmail(String email) throws UsernameNotFoundException {

        UserModel user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User [" + email + "] not found");
        }

        return user;
    }
}
