package com.project.core.model.administrative;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.project.core.utils.email.Email;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;
import org.springframework.security.core.GrantedAuthority;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.project.core.model.AbstractModel;

@Entity
@Audited
@Table(name = "TB_USER", indexes = @Index(name = "idx_username_email", columnList = "username, email"))
@JsonInclude(JsonInclude.Include.NON_NULL)
@TypeDef(name = "jsonbType", typeClass = JsonNodeType.class)
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
    private List<RoleModel> roles;

    @Type(type = "jsonbType")
    @Column(name = "report_exhibit_filter",columnDefinition = "jsonb")
    @Transient
    private Object reportExhibitFilter;

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

    public Object getReportExhibitFilter() {
        return reportExhibitFilter;
    }

    public void setReportExhibitFilter(Object reportExhibitFilter) {
        this.reportExhibitFilter = reportExhibitFilter;
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

    public List<RoleModel> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleModel> roles) {
        this.roles = roles;
    }



}
