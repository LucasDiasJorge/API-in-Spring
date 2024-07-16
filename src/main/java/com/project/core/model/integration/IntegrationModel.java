package com.project.core.model.integration;

import com.fasterxml.jackson.annotation.JsonView;
import com.project.core.enums.EAuthType;
import com.project.core.enums.EEntityType;
import com.project.core.model.AbstractModel;
import com.project.core.model.administrative.CompanyModel;
import com.project.core.utils.Views;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;

@Entity
@Table(name = "TB_INTEGRATION_ACTION")
@Audited
public class IntegrationModel extends AbstractModel {

    @ManyToOne
    @JoinColumn(name = "mirror_id")
    private MirrorModel mirror;

    @ManyToOne
    @JoinColumn(name = "mirror_auth_id")
    private MirrorModel mirrorAuth;

    @Column(name = "uri_trigger")
    private String uriTrigger;

    @Column(name = "method_trigger")
    private String methodTrigger;

    @Column(name = "uri")
    private String uri;

    @Column(name = "method")
    private String method;

    @Column(name = "uri_auth")
    private String uriAuth;

    @Column(name = "auth_route_type")
    @Enumerated(EnumType.STRING)
    private EAuthType authRouteType = EAuthType.NONE;

    @Column(name = "auth_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private EAuthType authType = EAuthType.NONE;

    @Column(name = "entity_type")
    @Enumerated(EnumType.STRING)
    private EEntityType entityType = EEntityType.NONE;

    @ManyToOne
    @JsonView(Views.Private.class)
    @JoinColumn(name = "company_id")
    private CompanyModel company;

    public IntegrationModel() {
    }

    public EAuthType getAuthRouteType() {
        return authRouteType;
    }

    public void setAuthRouteType(EAuthType authRouteType) {
        this.authRouteType = authRouteType;
    }

    public EEntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EEntityType entityType) {
        this.entityType = entityType;
    }

    public MirrorModel getMirrorAuth() {
        return mirrorAuth;
    }

    public void setMirrorAuth(MirrorModel mirrorAuth) {
        this.mirrorAuth = mirrorAuth;
    }

    public String getUriTrigger() {
        return uriTrigger;
    }

    public void setUriTrigger(String uriTrigger) {
        this.uriTrigger = uriTrigger;
    }

    public String getMethodTrigger() {
        return methodTrigger;
    }

    public void setMethodTrigger(String methodTrigger) {
        this.methodTrigger = methodTrigger;
    }

    public String getUriAuth() {
        return uriAuth;
    }

    public void setUriAuth(String uriAuth) {
        this.uriAuth = uriAuth;
    }

    public CompanyModel getCompany() {
        return company;
    }

    public void setCompany(CompanyModel company) {
        this.company = company;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public EAuthType getAuthType() {
        return authType;
    }

    public void setAuthType(EAuthType authType) {
        this.authType = authType;
    }

    public MirrorModel getMirror() {
        return mirror;
    }

    public void setMirror(MirrorModel mirror) {
        this.mirror = mirror;
    }
}
