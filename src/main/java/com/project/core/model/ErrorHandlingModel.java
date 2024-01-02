package com.project.core.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.project.core.model.administrative.CompanyModel;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Audited
@Table(name = "TB_ERRORS")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorHandlingModel extends AbstractModel {

    @Column(name = "message")
    private String message;

    @Column(name = "time",
            columnDefinition = "timestamp with time zone")
    @Temporal(TemporalType.TIMESTAMP)
    @Schema(type = "integer", format = "int64")
    private Date time;


    @ManyToOne
    @JoinColumn(name = "company_id")
    private CompanyModel company;

    public ErrorHandlingModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @JsonIncludeProperties({"id","companyName","fancyName"})
    public CompanyModel getCompany() {
        return company;
    }

    public void setCompany(CompanyModel company) {
        this.company = company;
    }

    @Override
    public String toString(){

        StringBuilder sb = new StringBuilder()
                .append("User ")
                .append(this.getCreatedBy())
                .append(" ")
                .append(" at ")
                .append(this.time)
                .append(" message: ")
                .append(this.message);

        return sb.toString();

    }
}
