package com.finzly.config_management.DTO;

public class TenantEnvDto {

    private String tenant;
    private String tenantName;
    private String environment;

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

    public TenantEnvDto(String tenant, String tenantName, String environment) {
        this.tenant = tenant;
        this.tenantName = tenantName;
        this.environment = environment;
    }
}