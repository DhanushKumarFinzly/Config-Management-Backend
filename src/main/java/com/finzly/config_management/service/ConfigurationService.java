package com.finzly.config_management.service;

import com.finzly.config_management.DTO.PropertyDTO;
import com.finzly.config_management.DTO.TenantEnvPropertiesDTO;
import com.finzly.config_management.Exception.ConfigurationSaveException;
import com.finzly.config_management.Exception.DataNotFoundException;
import com.finzly.config_management.Exception.UpdateFailedException;
import com.finzly.config_management.Repository.ConfigurationRepo;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.Configuration;
import com.finzly.config_management.model.TenantEnv;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ConfigurationService {
    @Autowired
    TenantEnvRepo tenantEnvRepo;
    @Autowired
    ConfigurationRepo configurationRepo;


    public List<PropertyDTO> getProperty(String tenant, String environment) throws DataNotFoundException {
        String tenantEnvId = tenantEnvRepo.findIdByTenantAndEnvironment(tenant, environment);
        if (tenantEnvId != null) {
            try {
                UUID uuid = UUID.fromString(tenantEnvId);
                List<Configuration> properties = configurationRepo.findByTenantEnvId(uuid);
                if (!properties.isEmpty()) {
                    return properties.stream()
                            .map(property -> new PropertyDTO(property.getId(), property.getPropertyKey(), property.getPropertyValue()))
                            .collect(Collectors.toList());
                } else {
                    throw new DataNotFoundException("No properties found for the given tenant-env ID: " + tenantEnvId);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format.");
            }
        } else {
            throw new DataNotFoundException("Tenant or environment not found.");
        }
    }

    public void saveTenantEnvProperties(TenantEnvPropertiesDTO tenantEnvPropertiesDTO) throws ConfigurationSaveException {
        try {
            String tenantEnvId = tenantEnvRepo.findIdByTenantAndEnvironment(tenantEnvPropertiesDTO.getTenant(), tenantEnvPropertiesDTO.getEnvironment());
            if (tenantEnvId != null) {
                UUID uuid = UUID.fromString(tenantEnvId);
                boolean keyExists = configurationRepo.existsByPropertyKeyAndTenantEnv(tenantEnvPropertiesDTO.getPropertyKey(), uuid);
                if (keyExists) {
                    throw new ConfigurationSaveException("PropertyKey already exists.");
                }
                Configuration configuration = new Configuration();
                configuration.setAppId("App123");
                configuration.setApplication("Application123");
                configuration.setFieldGroup("Global");
                configuration.setCreatedAt(LocalDateTime.now());
                configuration.setUpdatedAt(LocalDateTime.now());
                configuration.setIsSecureString(1);
                configuration.setStatus("Active");
                configuration.setProduct("Product123");
                configuration.setTarget("Config");
                configuration.setType("Environment");
                configuration.setPropertyKey(tenantEnvPropertiesDTO.getPropertyKey());
                configuration.setPropertyValue(tenantEnvPropertiesDTO.getPropertyValue());
                configuration.setTenantEnv(uuid);
                configurationRepo.save(configuration);
            } else {
                throw new DataNotFoundException("Tenant or environment not found.");
            }
        } catch (Exception e) {
            throw new ConfigurationSaveException(e.getMessage());
        }
    }

    public void deleteProperties(String uuid) {
        UUID id;
        try {
            id = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format provided for property ID: " + uuid, e);
        }
        Optional<Configuration> optionalConfiguration = configurationRepo.findById(id);
        if (optionalConfiguration.isPresent()) {
            configurationRepo.deleteById(id);
        } else {
            throw new EntityNotFoundException("Property not found.");
        }
    }

    public void updateProperties(PropertyDTO propertyDTO) throws UpdateFailedException {
        if (propertyDTO == null || isPropertyDTOEmpty(propertyDTO)) {
            throw new IllegalArgumentException("Property data cannot be null or empty.");
        }
        Optional<Configuration> properties = configurationRepo.findById(propertyDTO.getId());
        if (properties.isEmpty()) {
            throw new EntityNotFoundException("No properties found.");
        }
        try {
            Configuration existingProperty = properties.get();
            existingProperty.setPropertyKey(propertyDTO.getPropertyKey());
            existingProperty.setPropertyValue(propertyDTO.getPropertyValue());
            configurationRepo.save(existingProperty);
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to update the property with ID: '" + propertyDTO.getId() + "'. Details: " + e.getMessage());
        }
    }

    private boolean isPropertyDTOEmpty(PropertyDTO propertyDTO) {
        return propertyDTO.getPropertyKey() == null || propertyDTO.getPropertyKey().trim().isEmpty() ||
                propertyDTO.getPropertyValue() == null || propertyDTO.getPropertyValue().trim().isEmpty();
    }


    public List<Map<String, Object>> tenantEnvComparison(
            String tenant1, String environment1, String tenant2, String environment2) {

        // Fetch UUIDs for the provided tenants and environments
        String uuid1 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant1, environment1);
        String uuid2 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant2, environment2);

        // Initialize UUID variables
        UUID id1, id2;

        // Check if the UUIDs are found, else throw exceptions
        if (uuid1 == null) {
            throw new IllegalArgumentException(
                    "No ID Found For this Tenant: " + tenant1 + " and Environment: " + environment1);
        } else if (uuid2 == null) {
            throw new IllegalArgumentException(
                    "No ID Found For this Tenant: " + tenant2 + " and Environment: " + environment2);
        } else {
            id1 = UUID.fromString(uuid1);
            id2 = UUID.fromString(uuid2);
        }

        // Retrieve configuration properties for both tenants and environments
        List<Configuration> properties1 = configurationRepo.findByTenantEnvId(id1);
        List<Configuration> properties2 = configurationRepo.findByTenantEnvId(id2);

        // Convert lists of configurations to maps for easy comparison
        Map<String, String> tenant1Map = properties1.stream()
                .collect(Collectors.toMap(Configuration::getPropertyKey, Configuration::getPropertyValue, (existingValue, newValue) -> existingValue));
        Map<String, String> tenant2Map = properties2.stream()
                .collect(Collectors.toMap(Configuration::getPropertyKey, Configuration::getPropertyValue, (existingValue, newValue) -> existingValue));

        // Get a unified set of all property keysi
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(tenant1Map.keySet());
        allKeys.addAll(tenant2Map.keySet());

        // Prepare the result list for comparison
        List<Map<String, Object>> result = new ArrayList<>();

        // Iterate through all keys and compare their values between the two tenants
        for (String key : allKeys) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("propertyKey", key);
            entry.put("PropertyValue1", tenant1Map.getOrDefault(key, null));
            entry.put("PropertyValue2", tenant2Map.getOrDefault(key, null));

            // Check if the property values are the same (case insensitive)
            if (tenant1Map.get(key) != null && tenant2Map.get(key) != null &&
                    tenant1Map.get(key).toString().equalsIgnoreCase(tenant2Map.get(key).toString())) {
                entry.put("isSame", true);
            } else {
                entry.put("isSame", false);
            }

            // Add the comparison result to the final list
            result.add(entry);
        }

        // Return the comparison result
        return result;
    }

    public void changeProperty(String tenant, String environment, String propertyKey, String newValue) {
        // Fetch the UUID of tenant_env_id using tenant and environment
        String uuid = tenantEnvRepo.findIdByTenantAndEnvironment(tenant, environment);
        if (uuid == null) {
            throw new IllegalArgumentException("Invalid tenant or environment specified.");
        }

        UUID tenantEnvId = UUID.fromString(uuid);

        // Try to find the existing configuration for the tenantEnvId
        List<Configuration> configurations = configurationRepo.findByTenantEnvId(tenantEnvId);

        // Iterate through the configurations to find the matching key
        for (Configuration config : configurations) {
            if (config.getPropertyKey().equals(propertyKey)) {
                // Update the value for the matching property key
                config.setPropertyValue(newValue);
                configurationRepo.save(config);
                return; // Exit the method after updating
            }
        }

        // If no matching property key is found, add a new configuration
        addNewConfiguration(tenant, environment, propertyKey, newValue, tenantEnvId);
    }

    private void addNewConfiguration(String tenant, String environment, String propertyKey, String newValue, UUID tenantEnvId) {
        // Get the TenantEnv entity
        // Create a new Configuration object and set properties
        Configuration configuration = new Configuration();
        configuration.setAppId("finzly.ach");
        configuration.setApplication("Application123");
        configuration.setFieldGroup("Global");
        configuration.setCreatedAt(LocalDateTime.now());
        configuration.setUpdatedAt(LocalDateTime.now());
        configuration.setIsSecureString(1);
        configuration.setStatus("Active");
        configuration.setProduct("Product123");
        configuration.setTarget("Config");
        configuration.setType("Environment");
        configuration.setPropertyKey(propertyKey);
        configuration.setPropertyValue(newValue);
        configuration.setTenantEnv(tenantEnvId);

        // Save the new configuration
        configurationRepo.save(configuration);

    }




}