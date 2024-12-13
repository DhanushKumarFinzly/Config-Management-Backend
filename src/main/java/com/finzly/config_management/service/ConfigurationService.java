package com.finzly.config_management.service;

import com.finzly.config_management.DTO.CompareDTO;
import com.finzly.config_management.DTO.PropertyDTO;
import com.finzly.config_management.DTO.TenantEnvDto;
import com.finzly.config_management.DTO.TenantEnvPropertiesDTO;
import com.finzly.config_management.Exception.ConfigurationSaveException;
import com.finzly.config_management.Exception.DataNotFoundException;
import com.finzly.config_management.Exception.UpdateFailedException;
import com.finzly.config_management.Repository.ConfigurationRepo;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.Configuration;
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

    @Autowired
    TenantEnvService tenantEnvService;

    public List<PropertyDTO> getProperty(String tenant, String environment) throws DataNotFoundException {
        String tenantEnvId = tenantEnvRepo.findIdByTenantAndEnvironment(tenant, environment);
        if (tenantEnvId != null) {
            try{
                UUID uuid = UUID.fromString(tenantEnvId);
                List<Configuration> properties = configurationRepo.findByTenantEnvId(uuid);
                if (!properties.isEmpty()) {
                    return properties.stream()
                            .map(property -> new PropertyDTO(property.getId(), property.getPropertyKey(), property.getPropertyValue(), property.getApplication(), property.getFieldGroup(),property.getType(),property.getTarget()))
                            .collect(Collectors.toList());
                }else {
                    throw new DataNotFoundException("No properties found for the given tenant-env ID: " + tenantEnvId);
                }
            }catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format.");
            }
        }else {
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
                configuration.setApplication(tenantEnvPropertiesDTO.getApplication());
                configuration.setFieldGroup(tenantEnvPropertiesDTO.getField_group());
                configuration.setCreatedAt(LocalDateTime.now());
                configuration.setUpdatedAt(LocalDateTime.now());
                configuration.setIsSecureString(1);
                configuration.setStatus("Active");
                configuration.setProduct("Product123");
                configuration.setTarget(tenantEnvPropertiesDTO.getTarget());
                configuration.setType(tenantEnvPropertiesDTO.getType());
                configuration.setPropertyKey(tenantEnvPropertiesDTO.getPropertyKey());
                configuration.setPropertyValue(tenantEnvPropertiesDTO.getPropertyValue());
                configuration.setTenantEnv(uuid);
                configurationRepo.save(configuration);
            } else {
                throw new DataNotFoundException("Tenant or environment not found.");
            }
        }catch (Exception e) {
            throw new ConfigurationSaveException(e.getMessage());
        }
    }

    public void deleteProperties(String uuid) {
        UUID id;
        try {
            id = UUID.fromString(uuid);
        }catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format provided for property ID: " + uuid, e);
        }
        Optional<Configuration> optionalConfiguration = configurationRepo.findById(id);
        if (optionalConfiguration.isPresent()) {
            configurationRepo.deleteById(id);
        }else {
            throw new EntityNotFoundException("Property not found.");
        }
    }

    public void updateProperties(PropertyDTO propertyDTO) throws UpdateFailedException {
        if (propertyDTO == null || isPropertyDTOEmpty(propertyDTO)) {
            throw new IllegalArgumentException("Property data cannot be null or empty.");
        }
        Optional<Configuration> properties = configurationRepo.findById(propertyDTO.getId());
        if(properties.isEmpty()){
            throw new EntityNotFoundException("No properties found.");
        }
        try{
            Configuration existingProperty = properties.get();
            existingProperty.setPropertyKey(propertyDTO.getPropertyKey());
            existingProperty.setPropertyValue(propertyDTO.getPropertyValue());
            existingProperty.setApplication(propertyDTO.getApplication());
            existingProperty.setFieldGroup(propertyDTO.getFieldGroup());
            existingProperty.setTarget(propertyDTO.getTarget());
            existingProperty.setType(propertyDTO.getType());
            configurationRepo.save(existingProperty);
        }
        catch (Exception e) {
            throw new UpdateFailedException("Failed to update the property with ID: '" + propertyDTO.getId() + "'. Details: " + e.getMessage());
        }
    }

    private boolean isPropertyDTOEmpty(PropertyDTO propertyDTO) {
        return propertyDTO.getPropertyKey() == null || propertyDTO.getPropertyKey().trim().isEmpty() ||
                propertyDTO.getPropertyValue() == null || propertyDTO.getPropertyValue().trim().isEmpty();
    }

    public List<Map<String, Object>> tenantEnvComparison(
            List<Configuration> properties1,List<Configuration> properties2) {

        Map<String, String> tenant1Map = properties1.stream()
                .collect(Collectors.toMap(Configuration::getPropertyKey, Configuration::getPropertyValue, (existingValue, newValue) -> existingValue));
        Map<String, String> tenant2Map = properties2.stream()
                .collect(Collectors.toMap(Configuration::getPropertyKey, Configuration::getPropertyValue, (existingValue, newValue) -> existingValue));
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(tenant1Map.keySet());
        allKeys.addAll(tenant2Map.keySet());
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : allKeys) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("propertyKey", key);
            entry.put("PropertyValue1", tenant1Map.getOrDefault(key, null));
            entry.put("PropertyValue2", tenant2Map.getOrDefault(key, null));
            if (tenant1Map.get(key) != null && tenant2Map.get(key) != null &&
                    tenant1Map.get(key).toString().equalsIgnoreCase(tenant2Map.get(key).toString())) {
                entry.put("isSame", true);
            }
            else{
                entry.put("isSame", false);
            }
            result.add(entry);
        }
        return result;
    }



    public void changeProperty(String tenant, String environment, String propertyKey, String newValue) {
        String uuid = tenantEnvRepo.findIdByTenantAndEnvironment(tenant, environment);
        if (uuid == null) {
            throw new IllegalArgumentException("Invalid tenant or environment specified.");
        }
        UUID tenantEnvId = UUID.fromString(uuid);
        List<Configuration> configurations = configurationRepo.findByTenantEnvId(tenantEnvId);
        for (Configuration config : configurations) {
            if (config.getPropertyKey().equals(propertyKey)) {
                config.setPropertyValue(newValue);
                configurationRepo.save(config);
                return;
            }
        }
        addNewConfiguration(tenant, environment, propertyKey, newValue, tenantEnvId);
    }

    private void addNewConfiguration(String tenant, String environment, String propertyKey, String newValue, UUID tenantEnvId) {
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
        configurationRepo.save(configuration);
    }

    public void updateTenantAndEnv(String tenant, String environment) throws DataNotFoundException {
        String tenantEnvId = tenantEnvRepo.findIdByTenantAndEnvironment(tenant, environment);
        if (tenantEnvId != null) {
            try {
                UUID uuid = UUID.fromString(tenantEnvId);
                List<Configuration> properties = configurationRepo.findByTenantEnvId(uuid);
                if (!properties.isEmpty()) {
                    for (Configuration property : properties) {
                        String updatedPropertyKey = property.getPropertyKey();
                        if (updatedPropertyKey != null) {
                            if (updatedPropertyKey.contains("${env}")) {
                                updatedPropertyKey = updatedPropertyKey.replace("${env}", environment.toLowerCase());
                            }
                            if (updatedPropertyKey.contains("TENANT_NAME_ID")) {
                                updatedPropertyKey = updatedPropertyKey.replace("TENANT_NAME_ID", tenant.toLowerCase());
                            }
                            property.setPropertyKey(updatedPropertyKey);
                        }
                        if (property.getPropertyValue() != null) {
                            String updatedPropertyValue = property.getPropertyValue();
                            if (updatedPropertyValue.contains("${env}")) {
                                updatedPropertyValue = updatedPropertyValue.replace("${env}", environment.toLowerCase());
                            }
                            if (updatedPropertyValue.contains("TENANT_NAME_ID")) {
                                updatedPropertyValue = updatedPropertyValue.replace("TENANT_NAME_ID", tenant.toLowerCase());
                            }
                            property.setPropertyValue(updatedPropertyValue);
                        }
                    }
                    configurationRepo.saveAll(properties);
                } else {
                    throw new DataNotFoundException("No configurations found for the given tenant and environment.");
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID format for tenant_env_id.");
            }
        } else {
            throw new DataNotFoundException("Tenant or environment not found.");
        }
    }

    public void clonePropertyForNewTenant(String tenant1, String env1, String tenant2, String env2) {
        try {

            String uuid2 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant2, env2);

            if (uuid2 == null || uuid2.isEmpty()) {
                throw new IllegalArgumentException("No ID found for tenant: " + tenant1 + " and environment: " + env1);
            }

            tenant1 = tenant1.toLowerCase();
            env1 = env1.toLowerCase();
            tenant2 = tenant2.toLowerCase();
            env2 = env2.toLowerCase();
            UUID tenantEnvId2 = UUID.fromString(uuid2);


            List<String> applications=configurationRepo.findApplicationById(tenantEnvId2);
            List<String> fieldGroups=configurationRepo.findFieldGroupById(tenantEnvId2);
            TenantEnvDto tenantEnvDto = new TenantEnvDto();
            tenantEnvDto.setTenant(tenant1.toLowerCase());
            tenantEnvDto.setTenantName(tenant1.toLowerCase());// Set tenant1
            tenantEnvDto.setEnvironment(env1.toLowerCase());         // Set env1
            tenantEnvService.saveTenantEnv(tenantEnvDto);
            String uuid1 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant1.toUpperCase(), env1);
            if (uuid1 == null || uuid1.isEmpty()) {
                throw new IllegalArgumentException("No ID found for tenant: " + tenant1 + " and environment: " + env1);
            }
            UUID tenantEnvId1 = UUID.fromString(uuid1);
            List<Configuration> configurations = configurationRepo.findByTenantEnvId(tenantEnvId2);
            List<Configuration> newConfigurations = new ArrayList<>();
            for (Configuration config : configurations) {
                String updatedPropertyKey = config.getPropertyKey();
                String updatedPropertyValue = config.getPropertyValue();
                if (updatedPropertyKey != null) {
                    updatedPropertyKey = updatedPropertyKey.replace(env2, env1).replace(tenant2, tenant1);
                } else {
                    updatedPropertyKey = "NA";
                }
                if (updatedPropertyValue != null) {
                    updatedPropertyValue = updatedPropertyValue.replace(env2, env1).replace(tenant2, tenant1);
                } else {
                    updatedPropertyValue = "NA";
                }
                Configuration newConfiguration = new Configuration(
                        updatedPropertyKey,
                        config.getFieldGroup(),
                        config.getApplication(),
                        updatedPropertyValue,
                        config.getTarget(),
                        config.getType(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        config.getStatus(),
                        config.getProduct(),
                        config.getAppId(),
                        config.getIsSecureString(),
                        tenantEnvId1
                );
                newConfigurations.add(newConfiguration);
            }
            configurationRepo.saveAll(newConfigurations);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error while cloning properties: " + e.getMessage(), e);
        }
    }



    public CompareDTO   tenantEnvKeyComparison(
            String tenant1, String environment1, String tenant2, String environment2) {

        String uuid1 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant1, environment1);
        String uuid2 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant2, environment2);
        UUID id1, id2;
        String uuid=uuid2;
        if (uuid1 == null) {
            throw new IllegalArgumentException("No ID Found For this Tenant: " + tenant1 + " and Environment: " + environment1);
        } else if (uuid2 == null) {
            throw new IllegalArgumentException("No ID Found For this Tenant: " + tenant2 + " and Environment: " + environment2);
        } else {
            id1 = UUID.fromString(uuid1);
            id2 = UUID.fromString(uuid2);
        }

        List<Configuration> properties1 = configurationRepo.findByTenantEnvId(id1);
        List<Configuration> properties2 = configurationRepo.findByTenantEnvId(id2);

        List<Map<String, Object>> result = new ArrayList<>();

        // Iterate through properties1 using an iterator
        Iterator<Configuration> iterator1 = properties1.iterator();

        while (iterator1.hasNext()) {
            Configuration config1 = iterator1.next();
            String key1 = config1.getPropertyKey();

            // Nested iteration for properties2
            Iterator<Configuration> iterator2 = properties2.iterator();
            while (iterator2.hasNext()) {
                Configuration config2 = iterator2.next();
                String key2 = config2.getPropertyKey();

                if (key1.contains(tenant1) && key2.contains(tenant2)) {
                    // Normalize keys by replacing tenant names
                    String normalizedKey1 = key1.replace(tenant1, "{tenant}");
                    String normalizedKey2 = key2.replace(tenant2, "{tenant}");

                    // Check if normalized keys are identical
                    if (normalizedKey1.equals(normalizedKey2)) {
                        // Create result entries
                        Map<String, Object> list1Entry = new HashMap<>();
                        list1Entry.put("propertyKey", key1);
                        list1Entry.put("propertyValue", config1.getPropertyValue());

                        Map<String, Object> list2Entry = new HashMap<>();
                        list2Entry.put("propertyKey", key2);
                        list2Entry.put("propertyValue", config2.getPropertyValue());

                        // Add to result
                        result.add(list1Entry);
                        result.add(list2Entry);

                        // Remove matched entries from both lists
                        iterator1.remove(); // Remove current entry from properties1
                        iterator2.remove(); // Remove current entry from properties2

                        // Break inner loop since key1 is already matched
                        break;
                    }
                }
            }
        }
        List<Map<String, Object>> result1 =tenantEnvComparison(properties1,properties2);


        return new CompareDTO(result,result1);
    }

}