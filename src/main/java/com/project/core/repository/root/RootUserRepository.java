package com.project.core.repository.root;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.core.model.administrative.UserModel;
import com.project.core.repository.UserRepository;

public interface RootUserRepository extends UserRepository{

    @Query("SELECT u FROM UserModel u WHERE u.id =:id AND u.deletedBy IS NULL")
    public Optional<UserModel> findByIdNotDeleted(Long id);

    @Query("SELECT u FROM UserModel u WHERE u.deletedBy IS NULL ORDER BY u.id DESC")
    public List<UserModel> findAllRoot(Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE u.deletedBy IS NULL AND (LOWER(u.username) LIKE LOWER(CONCAT('%',:term,'%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%',:term,'%'))) ORDER BY u.id DESC")
    Optional<List<UserModel>> findByTermRoot(@Param("term") String term, Pageable pageable);


}
