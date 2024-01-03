package com.project.core.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.core.model.administrative.UserModel;

@Primary
public interface UserRepository extends JpaRepository<UserModel, Long> {

    public UserModel findByUsername(String username);

    @Query("SELECT u FROM UserModel u WHERE u.deletedBy IS NULL AND u.company.id = :companyId AND u.username = :username")
    public Optional<UserModel> findByUsernameAndCompanyId(@Param("username")String username, @Param("companyId") Long companyId);


    @Query("SELECT u FROM UserModel u WHERE u.id =:id AND u.deletedBy IS NULL")
    public Optional<UserModel> findByIdNotDeleted(Long id);

    @Query("SELECT u FROM UserModel u WHERE u.email = :email AND u.deletedBy IS NULL")
    public UserModel findByEmail(@Param("email")String email);

    @Query("SELECT u FROM UserModel u WHERE u.deletedBy IS NULL ORDER BY u.id DESC")
    public List<UserModel> findAllRoot(Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE u.company.id = :companyId AND u.deletedBy IS NULL ORDER BY u.id DESC")
    public Optional<List<UserModel>> findByCompanyId(@Param("companyId")Long companyId,Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE u.company.id = :companyId AND u.id = :id AND u.deletedBy IS NULL")
    UserModel findByIdCompany(@Param("companyId") Long companyId, @Param("id") Long id);

    @Query("SELECT u FROM UserModel u WHERE u.company.id = :companyId AND u.deletedBy IS NULL AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :term, '%'))) ORDER BY u.id DESC")
    Optional<List<UserModel>> findByTerm(@Param("companyId") Long companyId, @Param("term") String term, Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE u.company.id IN :ids AND u.deletedBy IS NULL ORDER BY u.id DESC")
    Optional<List<UserModel>> findByCompanyIdHeadquarters(@Param("ids") List<Long> ids, Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE u.company.id IN :ids AND u.deletedBy IS NULL AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :term, '%'))) ORDER BY u.id DESC")
    Optional<List<UserModel>> findByTermHeadquarters(@Param("ids") List<Long> ids,  @Param("term")  String term, Pageable pageable);

    @Query("SELECT u FROM UserModel u WHERE u.company.id IN :ids AND u.id = :id AND u.deletedBy IS NULL ORDER BY u.id")
    UserModel findByIdCompanyHeadquarters(@Param("ids") List<Long> ids,  @Param("id")  Long id);

    @Query(nativeQuery = true,value = "SELECT u.id,u.username,u.email FROM tb_users_roles ur JOIN tb_user u ON ur.user_id = u.id JOIN tb_role r ON ur.role_id = r.id WHERE u.company_id = :companyId AND r.role_name = :roleName")
    Optional<List<Map<String,Object>>> findByRole(String roleName, Long companyId);

    @Query("SELECT u FROM UserModel u WHERE u.email = :email AND u.deletedBy IS NULL")
    public Optional<UserModel> findByEmailOpt(@Param("email") String email);

    public Optional<UserModel> findByBiometryAndDeletedByIsNull(String biometry);

    public Optional<UserModel> findByCardAndDeletedByIsNull(String card);
}
