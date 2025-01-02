package com.finzly.config_management.DTO;

public class TenantEnvPropertiesDTO {
    private String tenant;
    private String environment;
    private String propertyKey;
    private String propertyValue;
    private String application;
    private String field_group;
    private String target;
    private String type;
    private String release;

    public TenantEnvPropertiesDTO(String tenant, String environment, String propertyKey, String propertyValue, String application, String field_group, String target, String type,String release) {
        this.tenant = tenant;
        this.environment = environment;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
        this.application = application;
        this.field_group = field_group;
        this.target = target;
        this.type = type;
        this.release=release;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getField_group() {
        return field_group;
    }

    public void setField_group(String field_group) {
        this.field_group = field_group;
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

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }
}