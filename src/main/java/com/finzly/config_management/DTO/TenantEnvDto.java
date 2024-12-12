package com.finzly.config_management.DTO;

public class TenantEnvDto {

    private String tenant;
    private String tenantName;
    private String environment;
    private String application;
    private String fieldGroup;



    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getFieldGroup() {
        return fieldGroup;
    }

    public void setFieldGroup(String fieldGroup) {
        this.fieldGroup = fieldGroup;
    }



    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public TenantEnvDto() {
    }

    public TenantEnvDto(String tenant, String tenantName, String environment, String application, String fieldGroup) {
        this.tenant = tenant;
        this.tenantName = tenantName;
        this.environment = environment;
        this.application = application;
        this.fieldGroup = fieldGroup;

    }

}