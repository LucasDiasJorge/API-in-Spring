package com.project.core.model.integration;

import com.project.core.model.AbstractModel;
import com.project.core.model.administrative.CompanyModel;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;

@Entity
@Audited
@Table(name = "TB_MIRROR")
public class MirrorModel extends AbstractModel {

    @Column(name = "map_convert")
    private String mapConvert;

    @Column(name = "auth_convert")
    private String authConvert;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyModel company;

    public MirrorModel() {
    }

    public String getMapConvert() {
        return mapConvert;
    }

    public void setMapConvert(String mapConvert) {
        this.mapConvert = mapConvert;
    }

    public String getAuthConvert() {
        return authConvert;
    }

    public void setAuthConvert(String authConvert) {
        this.authConvert = authConvert;
    }

    public CompanyModel getCompany() {
        return company;
    }

    public void setCompany(CompanyModel company) {
        this.company = company;
    }
}
