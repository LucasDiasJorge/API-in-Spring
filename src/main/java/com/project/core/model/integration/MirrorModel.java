package com.project.core.model.integration;

import com.project.core.model.AbstractModel;
import com.project.core.model.administrative.CompanyModel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
@Table(name = "TB_MIRROR")
public class MirrorModel extends AbstractModel {

    @Column(name = "details")
    private String details;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyModel company;

    public MirrorModel() {
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public CompanyModel getCompany() {
        return company;
    }

    public void setCompany(CompanyModel company) {
        this.company = company;
    }
}
