package com.project.core.model.integration;

import com.project.core.model.AbstractModel;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;

@Entity
@Table(name = "TB_INTEGRATION_ACTION")
@Audited
public class IntegrationModel extends AbstractModel {

    @Column(name = "message")
    private String message;

    @Column(name = "result")
    private Boolean result;

    @Column(name = "status_code")
    private Integer statusCode;

    @ManyToOne
    @JoinColumn(name = "mirror_id")
    private MirrorModel mirror;

    public IntegrationModel() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public MirrorModel getMirror() {
        return mirror;
    }

    public void setMirror(MirrorModel mirror) {
        this.mirror = mirror;
    }
}
