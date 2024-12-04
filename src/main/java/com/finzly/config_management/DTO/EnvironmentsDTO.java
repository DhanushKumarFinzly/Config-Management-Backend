package com.finzly.config_management.DTO;

import java.util.List;

public class EnvironmentsDTO {
    private String tenantName;
    private List<String> environments;

    public EnvironmentsDTO(String tenantName, List<String> environments) {
        this.tenantName = tenantName;
        this.environments = environments;
    }
    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }
}