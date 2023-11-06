package com.project.core.service;

import com.project.core.exception.throwable.AppException;
import com.project.core.model.ErrorHandlingModel;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.ErrorHandlingRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ErrorHandlingService {

    private final UserService userService;

    private final ErrorHandlingRepository errorHandlingRepository;

    public ErrorHandlingService(UserService userService, ErrorHandlingRepository errorHandlingRepository) {
        this.userService = userService;
        this.errorHandlingRepository = errorHandlingRepository;
    }

    public List<ErrorHandlingModel> findAll(Principal principal, int limit, int offset) {
        UserModel userModel = userService.loadUserByEmail(principal.getName());

        Pageable pageable = PageRequest.of(offset, limit);

        List<ErrorHandlingModel> errors = errorHandlingRepository.findByCompanyId(userModel.getCompany().getId(),pageable);

        return errors;
    }

    public ErrorHandlingModel create(Principal principal, Map<String,Object> body) throws AppException {
        UserModel userModel = userService.loadUserByEmail(principal.getName());

        ErrorHandlingModel errorHandlingModel = new ErrorHandlingModel();

        Date now = new Date();

        try {

            errorHandlingModel.setCreatedBy(userModel.getId());
            errorHandlingModel.setCompany(userModel.getCompany());
            errorHandlingModel.setMessage((String) body.get("message"));
            errorHandlingModel.setTime(new Date(((Number) body.get("time")).longValue()));
            errorHandlingModel.setCreatedAt(now);
            errorHandlingModel.setDeletedAt(new Date(0));
            errorHandlingRepository.save(errorHandlingModel);
        } catch (Exception e) {
            throw new AppException(422,e.toString());
        }

        return errorHandlingModel;

    }
}
