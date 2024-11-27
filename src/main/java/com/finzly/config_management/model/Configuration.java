package com.finzly.config_management.model;


import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    @Column(name = "property_key")
    private String propertyKey;

    @Column(name = "field_group")
    private String fieldGroup;

    @Column(name = "application")
    private String application;

    @Column(name = "property_value")
    private String propertyValue;

    @Column(name = "target")
    private String target;

    @Column(name = "type")
    private String type;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "status")
    private String status;

    @Column(name = "product")
    private String product;

    @Column(name = "app_id")
    private String appId;


    @Column(name = "is_secure_string")
    private int isSecureString;

    @ManyToOne
    @JoinColumn(name = "tenant_env_id", referencedColumnName = "id", nullable = false) // Foreign key column
    private TenantEnv tenantEnv;


    public Configuration() {
    }

    public Configuration(String propertyKey, String fieldGroup, String application, String propertyValue, String target, String type, LocalDateTime createdOn, LocalDateTime updatedOn, String status, String product, String appId, int isSecureString, TenantEnv tenantEnv) {
        this.propertyKey = propertyKey;
        this.fieldGroup = fieldGroup;
        this.application = application;
        this.propertyValue = propertyValue;
        this.target = target;
        this.type = type;
        this.createdOn = createdOn;
        this.updatedOn = updatedOn;
        this.status = status;
        this.product = product;
        this.appId = appId;
        this.isSecureString = isSecureString;
        this.tenantEnv = tenantEnv;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getFieldGroup() {
        return fieldGroup;
    }

    public void setFieldGroup(String fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public int getIsSecureString() {
        return isSecureString;
    }

    public void setIsSecureString(int isSecureString) {
        this.isSecureString = isSecureString;
    }

    public TenantEnv getTenantEnv() {
        return tenantEnv;
    }

    public void setTenantEnv(TenantEnv tenantEnv) {
        this.tenantEnv = tenantEnv;
    }




}