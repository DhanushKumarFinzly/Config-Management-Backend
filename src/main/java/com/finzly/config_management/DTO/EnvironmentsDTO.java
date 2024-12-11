package com.finzly.config_management.DTO;

import java.util.List;

public class EnvironmentsDTO {
    private String tenantName;
    private List<String> environments;
//    private String application;
//    private String fieldGroup;

    public EnvironmentsDTO() {

    }

    public EnvironmentsDTO(String tenantName, List<String> environments, String application, String fieldGroup) {
        this.tenantName = tenantName;
        this.environments = environments;
//        this.application = application;
//        this.fieldGroup = fieldGroup;
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

//    public String getApplication() {
//        return application;
//    }
//
//    public void setApplication(String application) {
//        this.application = application;
//    }
//
//    public String getFieldGroup() {
//        return fieldGroup;
//    }
//
//    public void setFieldGroup(String fieldGroup) {
//        this.fieldGroup = fieldGroup;
//    }

}