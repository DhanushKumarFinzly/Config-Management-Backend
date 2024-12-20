package com.finzly.config_management.DTO;

import java.util.List;
import java.util.Map;

public class CompareDTO {
    private List<PropertyComparisonDTO> tenantBasedProperties;
    private List<Map<String,Object>> commonProperties;

    public List<PropertyComparisonDTO> getTenantBasedProperties() {
        return tenantBasedProperties;
    }

    public void setTenantBasedProperties(List<PropertyComparisonDTO> tenantBasedProperties) {
        this.tenantBasedProperties = tenantBasedProperties;
    }

    public List<Map<String, Object>> getCommonProperties() {
        return commonProperties;
    }

    public void setCommonProperties(List<Map<String, Object>> commonProperties) {
        this.commonProperties = commonProperties;
    }

    public CompareDTO(List<PropertyComparisonDTO> tenantBasedProperties, List<Map<String, Object>> commonProperties) {
        this.tenantBasedProperties = tenantBasedProperties;
        this.commonProperties = commonProperties;
    }
}
