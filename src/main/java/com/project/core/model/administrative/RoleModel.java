package com.project.core.model.administrative;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import com.project.core.enums.RoleName;

@Entity
@Table(name = "TB_ROLE")
@Audited
public class RoleModel implements GrantedAuthority{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleName roleName;

    private Integer priority;

    public RoleModel(RoleName roleName) {
        this.roleName = roleName;
    }

    public RoleModel() {

    }

    @Override
    public String getAuthority() {
        return this.roleName.toString();
    }

    public Long getId() {
        return id;
    }

    public void setRoleId(Long id) {
        this.id = id;
    }

    public RoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(RoleName roleName) {
        this.roleName = roleName;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }


}
