package com.project.core.model.administrative;

import com.project.core.model.AbstractModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import javax.persistence.*;
import javax.validation.constraints.Email;

import org.hibernate.envers.Audited;

import java.util.List;

@Entity
@Table(name = "TB_COMPANY")
@Audited
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CompanyModel extends AbstractModel {

    private String clientKey;

    @Column(nullable = false)
    private String fancyName;

    @Column(nullable = false)
    private String companyName;

    private String managerName;

    @Email(message = "Email is not valid")
    private String managerEmail;

    @Column(name = "company_phone")
    private String companyPhone;

    @Column(name = "epc_prefix", length = 16)
    private String epcPrefix;

    @Column(name = "cnpj", length = 14, nullable = false)
    private String cnpj;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "headquarters", nullable = false)
    private Boolean headquarters = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "TB_HEAD_BRANCHS")
    private List<CompanyModel> branchs;

    @Column(name = "moving_item_creation", nullable = false)
    private int movingItemCreation = 0;

    @Column(name = "integration")
    private Boolean integration;

    @Column(name = "identifier")
    private String identifier;
    

    public CompanyModel(){
    }

    public Boolean getHeadquarters() {
        return headquarters;
    }

    public Boolean getIntegration() {
        return integration;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setIntegration(Boolean integration) {
        this.integration = integration;
    }

    public Boolean isHeadquarters() {
        return headquarters;
    }

    public void setHeadquarters(Boolean headquarters) {
        this.headquarters = headquarters;
    }

    @JsonIncludeProperties({"id","companyName","fancyName","managerEmail","managerName","cnpj"})
    public List<CompanyModel> getBranchs() {
        return branchs;
    }

    public void setBranchs(List<CompanyModel> branchs) {
        this.branchs = branchs;
    }

    public String getClientKey() {
        return clientKey;
    }

    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

    public String getFancyName() {
        return fancyName;
    }

    public void setFancyName(String fancyName) {
        this.fancyName = fancyName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getEpcPrefix() {
        return epcPrefix;
    }

    public void setEpcPrefix(String epcPrefix) {
        this.epcPrefix = epcPrefix;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getMovingItemCreation() {
        return movingItemCreation;
    }

    public void setMovingItemCreation(int movingItemCreation) {
        this.movingItemCreation = movingItemCreation;
    }

}