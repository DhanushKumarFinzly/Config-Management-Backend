package com.finzly.config_management.DTO;

import java.util.UUID;

public class PropertyDTO {
    private UUID id;
    private String propertyKey;
    private String propertyValue;
    private String application;
    private String fieldGroup;
    private String type;
    private String target;
    

    public PropertyDTO(UUID id, String propertyKey, String propertyValue, String application, String fieldGroup, String type, String target) {
        this.id = id;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
        this.application = application;
        this.fieldGroup = fieldGroup;
        this.type = type;
        this.target = target;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getFieldGroup() {
        return fieldGroup;
    }

    public void setFieldGroup(String fieldGroup) {
        this.fieldGroup = fieldGroup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }




}