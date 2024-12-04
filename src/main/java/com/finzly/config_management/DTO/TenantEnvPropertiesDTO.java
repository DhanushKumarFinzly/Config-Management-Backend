package com.finzly.config_management.DTO;

public class TenantEnvPropertiesDTO {
    private String tenant;
    private String environment;
    private String propertyKey;
    private String propertyValue;

    public TenantEnvPropertiesDTO(String tenant, String environment, String propertyKey, String propertyValue) {
        this.tenant = tenant;
        this.environment = environment;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
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
}