package com.finzly.config_management.DTO;

public class TenantEnvDto {

    private String tenant;
    private String tenantName;
    private String environment;
    private String release;


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

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public TenantEnvDto(String tenant, String tenantName, String environment,String release) {
        this.tenant = tenant;
        this.tenantName = tenantName;
        this.environment = environment;
        this.release=release;
    }

}