package com.finzly.config_management.DTO;

public class TenantDto {

    private String tenant;
    private String tenantName;
    private String status;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    public TenantDto(String tenant, String tenantName,String status) {
        this.tenant = tenant;
        this.tenantName = tenantName;
        this.status=status;
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
}