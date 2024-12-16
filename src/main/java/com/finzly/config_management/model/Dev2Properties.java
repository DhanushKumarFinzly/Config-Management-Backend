package com.finzly.config_management.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "dev2_properties")
public class Dev2Properties {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "field_group", length = 500)
    private String fieldGroup;

    @Column(name = "application", length = 500)
    private String application;

    @Column(name = "profile", length = 500)
    private String profile;

    @Column(name = "label", length = 500)
    private String label;

    @Column(name = "prop_key", length = 500)
    private String propKey;

    @Column(name = "value", length = 3072)
    private String value;

    @Column(name = "property_type", length = 50)
    private String propertyType;

    @Column(name = "secret")
    private Boolean secret;

    @Column(name = "last_updated_by", length = 500)
    private String lastUpdatedBy;

    @Column(name = "last_updated_date")
    private LocalDateTime lastUpdatedDate;

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public Boolean getSecret() {
        return secret;
    }

    public void setSecret(Boolean secret) {
        this.secret = secret;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public LocalDateTime getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDateTime lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }
    public Dev2Properties() {
    }

    public Dev2Properties(String id, String fieldGroup, String application, String profile, String label, String propKey, String value, String propertyType, Boolean secret, String lastUpdatedBy, LocalDateTime lastUpdatedDate) {
        this.id = id;
        this.fieldGroup = fieldGroup;
        this.application = application;
        this.profile = profile;
        this.label = label;
        this.propKey = propKey;
        this.value = value;
        this.propertyType = propertyType;
        this.secret = secret;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedDate = lastUpdatedDate;
    }
}
