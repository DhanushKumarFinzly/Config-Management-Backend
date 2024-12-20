package com.finzly.config_management.DTO;

public class InterChangeDTO {

    private String tenant;
    private String environment;
    private String propertyKey;
    private String newValue;

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

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public InterChangeDTO(String tenant, String environment, String propertyKey, String newValue) {
        this.tenant = tenant;
        this.environment = environment;
        this.propertyKey = propertyKey;
        this.newValue = newValue;
    }
}
