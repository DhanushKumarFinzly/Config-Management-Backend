package com.finzly.config_management.DTO;

public class PropertyComparisonDTO {
    private String masterKey;
    private String propertyKey1;
    private String value1;
    private String propertyKey2;
    private String value2;
    private Boolean isSame;
    // Getters and Setters
    public String getMasterKey() {
        return masterKey;
    }

    public void setMasterKey(String masterKey) {
        this.masterKey = masterKey;
    }

    public String getPropertyKey1() {
        return propertyKey1;
    }

    public void setPropertyKey1(String propertyKey1) {
        this.propertyKey1 = propertyKey1;
    }

    public String getValue1() {
        return value1;
    }

    public void setValue1(String value1) {
        this.value1 = value1;
    }

    public String getPropertyKey2() {
        return propertyKey2;
    }

    public void setPropertyKey2(String propertyKey2) {
        this.propertyKey2 = propertyKey2;
    }

    public String getValue2() {
        return value2;
    }

    public void setValue2(String value2) {
        this.value2 = value2;
    }

    public Boolean getIsSame() {
        return isSame;
    }

    public void setIsSame(Boolean same) {
        isSame = same;
    }

    public PropertyComparisonDTO() {
    }

    public PropertyComparisonDTO(String masterKey, String propertyKey1, String value1, String propertyKey2, String value2, Boolean isSame) {
        this.masterKey = masterKey;
        this.propertyKey1 = propertyKey1;
        this.value1 = value1;
        this.propertyKey2 = propertyKey2;
        this.value2 = value2;
        this.isSame = isSame;
    }
}
