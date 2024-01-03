package com.project.core.model.administrative;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.project.core.utils.email.Email;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.core.model.AbstractModel;

@Entity
@Audited
@Table(name = "TB_USER", indexes = @Index(name = "idx_username_email", columnList = "username, email"))
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserModel extends AbstractModel {

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyModel company;

    private String username;

    @Email
    @Column(unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String pass;
    

    @Column(name = "biometry", columnDefinition = "TEXT")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String biometry;

    @Column(name = "card", columnDefinition = "TEXT")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String card;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinTable(name = "TB_USERS_ROLES",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleModel> roles;


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles;
    }

    private Date lastLogin;

    private boolean active = true;

    private int attempts = 0;

    public UserModel() {
    }

    @JsonIncludeProperties({ "id", "companyName", "fancyName", "managerEmail", "managerName", "cnpj" })
    public CompanyModel getCompany() {
        return company;
    }

    public String getBiometry() {
        return biometry;
    }

    public void setBiometry(String biometry) {
        this.biometry = biometry;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public void setCompany(CompanyModel company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(Set<RoleModel> roles) {
        this.roles = roles;
    }

    public Set<RoleModel> getRoles() {
        return roles;
    }
}
