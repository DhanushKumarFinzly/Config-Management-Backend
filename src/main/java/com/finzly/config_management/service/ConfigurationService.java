package com.finzly.config_management.service;

import com.finzly.config_management.DTO.PropertyDTO;
import com.finzly.config_management.Exception.DataNotFoundException;
import com.finzly.config_management.Repository.ConfigurationRepo;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConfigurationService {
    @Autowired
    TenantEnvRepo tenantEnvRepo;
    @Autowired
    ConfigurationRepo configurationRepo;
    public List<PropertyDTO> getProperty(String tenant, String environment) throws DataNotFoundException {
        // Fetch the tenantEnvId using tenant and environment
        String tenantEnvId = tenantEnvRepo.findIdByTenantAndEnvironment(tenant, environment);

        if (tenantEnvId != null) {
            try {
                // Convert tenantEnvId to UUID
                UUID uuid = UUID.fromString(tenantEnvId);

                // Fetch the configuration by UUID
                List<Configuration> properties= configurationRepo.findByTenantEnvId(uuid);

                if (!properties.isEmpty()) {

                    // Build PropertyDTO list from the Configuration object
                    List<PropertyDTO> propertyList = new ArrayList<>();
                    properties.forEach(property ->
                            propertyList.add(new PropertyDTO(property.getId(), property.getPropertyKey(), property.getPropertyValue()))
                    );
                    return propertyList;
                } else {
                    throw new DataNotFoundException("Property not found for tenantEnvId: " + tenantEnvId);
                }
            }
            catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid UUID format for tenantEnvId: " + tenantEnvId, e);
            }
        }
        else {
            throw new DataNotFoundException("TenantEnv ID not found for tenant: " + tenant + " and environment: " + environment);
        }
    }

}
