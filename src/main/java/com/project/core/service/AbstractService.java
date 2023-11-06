package com.project.core.service;

import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.project.core.exception.throwable.AppException;
import com.project.core.model.AbstractModel;
import com.project.core.model.administrative.UserModel;
import com.project.core.repository.UserRepository;

@Service
public abstract class AbstractService<T extends AbstractModel> {

    private JpaRepository<T, Long> repository;

    @Autowired
    private UserRepository userRepository;

    protected AbstractService(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public List<T> findAll() throws AppException {
        try {
            return repository.findAll();
        } catch (Exception e) {
            throw new AppException("Error while GET entity",e.getCause(),400,null);
        }
    }

    public T findById(Long id) throws AppException {
        return repository.findById(id)
                .orElseThrow(() -> new AppException("Id not found",400,null));
    }

    public T save(T entity, Principal principal) throws AppException {
        UserModel user = getUserFromPrincipal(principal);
        try {
            entity.setLastModifiedDate(new Date());
            entity.setDeletedAt(new Date(0));
            entity.setCreatedAt(new Date());
            entity.setUpdatedBy(user.getId());
            return repository.save(entity);
        } catch (Exception e) {
            throw new AppException("Error while creating new " + entity.getClass().getSimpleName(),e.getCause(),400,null);
        }

    }

    public T save(T entity) throws AppException {
        try {
            entity.setLastModifiedDate(new Date());
            entity.setDeletedAt(new Date(0));
            entity.setCreatedAt(new Date());
            return repository.save(entity);
        } catch (Exception e) {
            throw new AppException("Error while creating new " + entity.getClass().getSimpleName(),e.getCause(),400,null);
        }

    }

    public T update(Long id, T entity, T existingEntity, Principal principal) throws AppException {
        UserModel user = getUserFromPrincipal(principal);
        try {
            BeanUtils.copyProperties(entity, existingEntity, "id","companyId", "deletedAt", "createdAt", "lastModifiedDate");
            existingEntity.setUpdatedBy(user.getId());
            existingEntity.setLastModifiedDate(new Date());
            return repository.save(existingEntity);
        } catch (Exception e) {
            throw new AppException("Error while updating new " + entity.getClass().getSimpleName(),e.getCause(),400,null);
        }

    }

    public T update(Long id, T entity) {
        return repository.save(entity);
    }

    public T delete(Long id, T entity, Principal principal) throws AppException {
        UserModel user = getUserFromPrincipal(principal);
        try{
            entity.setDeletedBy(user.getId());
            entity.setDeletedAt(new Date());
            return repository.save(entity);
        }catch(Exception e){
            throw new AppException("Error while deleting " + entity.getClass().getSimpleName(),e.getCause(),400,null);
        }
    }

    public UserModel getUserFromPrincipal(Principal principal){
        if(principal != null){
            return userRepository.findByEmail(principal.getName());
        }else{
            return null;
        }
    }
}