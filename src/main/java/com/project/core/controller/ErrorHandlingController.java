package com.project.core.controller;

import com.project.core.dto.Response;
import com.project.core.exception.throwable.AppException;
import com.project.core.model.ErrorHandlingModel;
import com.project.core.service.ErrorHandlingService;
import com.project.core.utils.ResponseUtil;
import org.hibernate.TransactionException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/errors")
public class ErrorHandlingController {

    private final ErrorHandlingService errorHandlingService;

    public ErrorHandlingController(ErrorHandlingService errorHandlingService) {
        this.errorHandlingService = errorHandlingService;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ROOT')")
    @GetMapping
    public ResponseEntity<Response<List<ErrorHandlingModel>>> findAll(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "10000") @Valid int limit,
            Principal user) throws AppException, TransactionException, InterruptedException, IOException {

        return ResponseUtil.createResponse(errorHandlingService.findAll(user,limit,offset), 200, "200 OK", user);

    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_ROOT')")
    @PostMapping
    public ResponseEntity<Response<ErrorHandlingModel>> create(@RequestBody Map<String,Object> body , Principal user) throws TransactionException, AppException {

        return ResponseUtil.createResponse(errorHandlingService.create(user,body), 200, "200 OK", user);

    }


}
