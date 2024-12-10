package com.finzly.config_management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "master_configuration")
public class MasterConfiguration {

    @Id
    @Column(name = "property_key",length = 255)
    private String propertyKey;

    @Column(name = "field_group",length = 255)
    private String fieldGroup; // Representing ENUM as String (Global, Customer, Application)

    @Column(name = "application", length = 255)
    private String application;

    @Column(name = "property_value", length = 5000)
    private String propertyValue;

    @Column(name = "target",length = 255)
    private String target;

    @Column(name = "type", length = 255)
    private String type;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "updated_on")
    private LocalDateTime updatedOn;

    @Column(name = "status", length = 255)
    private String status;

    @Column(name = "product", length = 45)
    private String product;

    @Column(name = "app_id", length = 45)
    private String appId;

    @Column(name = "is_secure_string")
    private int isSecureString;

    // Default Constructor
    public MasterConfiguration() {
    }

    // Parameterized Constructor
    public MasterConfiguration(
            String propertyKey,
            String fieldGroup,
            String application,
            String propertyValue,
            String target,
            String type,
            LocalDateTime createdOn,
            LocalDateTime updatedOn,
            String status,
            String product,
            String appId,
            int isSecureString) {
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
    }

    // Getters and Setters
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
}
