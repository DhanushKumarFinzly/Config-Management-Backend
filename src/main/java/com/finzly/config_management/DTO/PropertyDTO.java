package com.finzly.config_management.DTO;

import java.util.UUID;

public class PropertyDTO {
    private UUID id;
    private String propertyKey;
    private String propertyValue;

    public PropertyDTO(UUID id, String propertyKey, String propertyValue) {
        this.id = id;
        this.propertyKey = propertyKey;
        this.propertyValue = propertyValue;
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



}