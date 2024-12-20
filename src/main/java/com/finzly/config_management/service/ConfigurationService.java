package com.finzly.config_management.service;

import com.finzly.config_management.DTO.*;
import com.finzly.config_management.Exception.ConfigurationSaveException;
import com.finzly.config_management.Exception.DataNotFoundException;
import com.finzly.config_management.Exception.UpdateFailedException;
import com.finzly.config_management.Repository.*;
import com.finzly.config_management.controller.BaseProperties;
import com.finzly.config_management.model.*;
import jakarta.persistence.EntityNotFoundException;
import org.aspectj.weaver.ast.Test;
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

    @Autowired
    Dev2PropertiesRepo dev2PropertiesRepo;

    @Autowired
    DevPropertiesRepo devPropertiesRepo;

    @Autowired
    TestPropertiesRepo testPropertiesRepo;

    @Autowired
    Test2PropertiesRepo test2PropertiesRepo;

    @Autowired
    MasterConfigurationRepo masterConfigurationRepo;

    private static final String TENANT_PLACEHOLDER = "TENANT_NAME_ID";
    private static final String ENV_PLACEHOLDER="${env}";

    public List<PropertyDTO> getProperty(String tenant, String environment) throws DataNotFoundException {
        try {
            UUID tenantEnvId = getTenantEnvId(tenant, environment); // Exception propagates here
            List<Configuration> properties = configurationRepo.findByTenantEnvId(tenantEnvId);
            if (properties.isEmpty()) {
                throw new DataNotFoundException("No properties found for the given tenant-env ID: " + tenantEnvId);
            }
            return properties.stream()
                    .map(property -> new PropertyDTO(property.getId(), property.getPropertyKey(), property.getPropertyValue(),
                            property.getApplication(), property.getFieldGroup(), property.getType(), property.getTarget()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new DataNotFoundException("Invalid tenant or environment provided: " + e.getMessage());
        }
    }

    public void saveTenantEnvProperties(TenantEnvPropertiesDTO tenantEnvPropertiesDTO) throws ConfigurationSaveException {
        try {
            UUID tenantEnvId = getTenantEnvId(tenantEnvPropertiesDTO.getTenant(), tenantEnvPropertiesDTO.getEnvironment());
            boolean keyExists = configurationRepo.existsByPropertyKeyAndTenantEnv(tenantEnvPropertiesDTO.getPropertyKey(), tenantEnvId);
            if (keyExists) {
                throw new ConfigurationSaveException("PropertyKey already exists.");
            }
            saveConfiguration(tenantEnvPropertiesDTO, tenantEnvId);
        } catch (ConfigurationSaveException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new ConfigurationSaveException("Invalid tenant or environment details: " + e.getMessage());
        } catch (Exception e) {
            throw new ConfigurationSaveException("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void saveConfiguration(TenantEnvPropertiesDTO tenantEnvPropertiesDTO, UUID tenantEnvId) throws ConfigurationSaveException {
        try {

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
            configuration.setTenantEnv(tenantEnvId);
            configurationRepo.save(configuration);
        } catch (Exception e) {
            throw new ConfigurationSaveException("Failed to save configuration: " + e.getMessage());
        }
    }

    public void deleteProperties(String uuid) {
        try {
            UUID id = UUID.fromString(uuid); // Parse UUID and handle invalid format
            Configuration configuration = configurationRepo.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Property not found for ID: " + uuid));
            configurationRepo.delete(configuration); // Delete the entity
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format provided for property ID: " + uuid);
        }
    }


    public void updateProperties(PropertyDTO propertyDTO) throws UpdateFailedException {

        if (propertyDTO == null || isPropertyDTOEmpty(propertyDTO)) {
            throw new IllegalArgumentException("Property data cannot be null or empty.");
        }
        try {
            Configuration existingProperty = configurationRepo.findById(propertyDTO.getId())
                    .orElseThrow(() -> new EntityNotFoundException("No property found with ID: " + propertyDTO.getId()));
            existingProperty.setPropertyKey(propertyDTO.getPropertyKey());
            existingProperty.setPropertyValue(propertyDTO.getPropertyValue());
            existingProperty.setApplication(propertyDTO.getApplication());
            existingProperty.setFieldGroup(propertyDTO.getFieldGroup());
            existingProperty.setTarget(propertyDTO.getTarget());
            existingProperty.setType(propertyDTO.getType());
            configurationRepo.save(existingProperty);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to update the property with ID: '" + propertyDTO.getId() + "'. Details: " + e.getMessage());
        }
    }

    private boolean isPropertyDTOEmpty(PropertyDTO propertyDTO) {
        return propertyDTO.getPropertyKey() == null || propertyDTO.getPropertyKey().trim().isEmpty() ||
                propertyDTO.getPropertyValue() == null || propertyDTO.getPropertyValue().trim().isEmpty();
    }

    public List<Map<String, Object>> tenantEnvComparison(
            List<Configuration> properties1, List<Configuration> properties2) {

        // Convert properties1 to a map
        Map<String, String> tenant1Map =  properties1.stream()
                .collect(Collectors.toMap(
                        config -> config.getPropertyKey() != null ? config.getPropertyKey() : "NA",
                        config -> config.getPropertyValue() != null ? config.getPropertyValue() : "NA",
                        (existingValue, newValue) -> existingValue
                ));

        // Convert properties2 to a map
        Map<String, String> tenant2Map = properties1.stream()
                .collect(Collectors.toMap(
                        config -> config.getPropertyKey() != null ? config.getPropertyKey() : "NA",
                        config -> config.getPropertyValue() != null ? config.getPropertyValue() : "NA",
                        (existingValue, newValue) -> existingValue
                ));

        // Combine keys from both maps
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(tenant1Map.keySet());
        allKeys.addAll(tenant2Map.keySet());

        // Build the result list
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : allKeys) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("propertyKey", key);
            entry.put("PropertyValue1", tenant1Map.getOrDefault(key, "NA"));
            entry.put("PropertyValue2", tenant2Map.getOrDefault(key, "NA"));
            entry.put("isSame", tenant1Map.getOrDefault(key, "NA")
                    .equalsIgnoreCase(tenant2Map.getOrDefault(key, "NA")));
            result.add(entry);
        }

        return result;
    }



    public void changeProperty(InterChangeDTO interChangeDTO) {
        String uuid = tenantEnvRepo.findIdByTenantAndEnvironment(interChangeDTO.getTenant(), interChangeDTO.getEnvironment());
        if (uuid == null) {
            throw new IllegalArgumentException("Invalid tenant or environment specified.");
        }
        UUID tenantEnvId = UUID.fromString(uuid);
        List<Configuration> configurations = configurationRepo.findByTenantEnvId(tenantEnvId);
        for (Configuration config : configurations) {
            if (config.getPropertyKey().equals(interChangeDTO.getPropertyKey())){
                config.setPropertyValue(interChangeDTO.getNewValue());
                configurationRepo.save(config);
                return;
            }
        }
       addNewConfiguration(interChangeDTO.getTenant(), interChangeDTO.getEnvironment(), interChangeDTO.getPropertyKey(), interChangeDTO.getNewValue(), tenantEnvId);
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



    public void clonePropertyForNewTenant(String tenant1, String env1, String tenant2, String env2) {
        try {
            UUID tenantEnvId2 = getTenantEnvId(tenant2, env2);
            tenant1 = tenant1.toLowerCase();
            env1 = env1.toLowerCase();
            tenant2 = tenant2.toLowerCase();
            env2 = env2.toLowerCase();
            TenantEnvDto tenantEnvDto=createTenantEnvDto(tenant1,env1);
            tenantEnvService.saveTenantEnv(tenantEnvDto);
            UUID tenantEnvId1= getTenantEnvId(tenant1.toUpperCase(),env1);
            List<Configuration> configurations = configurationRepo.findByTenantEnvId(tenantEnvId2);
            List<Configuration> newConfigurations = new ArrayList<>();
            for (Configuration config : configurations) {
                String updatedPropertyKey =replaceNewTenantAndEnvName(config.getPropertyKey(),tenant1,env1,tenant2,env2);
                String updatedPropertyValue =replaceNewTenantAndEnvName(config.getPropertyValue(),tenant1,env1,tenant2,env2);
                Configuration newConfiguration = new Configuration(updatedPropertyKey, config.getFieldGroup(),
                        config.getApplication(), updatedPropertyValue, config.getTarget(), config.getType(),
                        LocalDateTime.now(), LocalDateTime.now(), config.getStatus(), config.getProduct(),
                        config.getAppId(), config.getIsSecureString(), tenantEnvId1
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

    private TenantEnvDto createTenantEnvDto(String tenant,String env){
        TenantEnvDto tenantEnvDto = new TenantEnvDto();
        tenantEnvDto.setTenant(tenant.toLowerCase());
        tenantEnvDto.setTenantName(tenant.toLowerCase());// Set tenant1
        tenantEnvDto.setEnvironment(env.toLowerCase());
        return tenantEnvDto;
    }

    private String replaceNewTenantAndEnvName(String property,String tenant1,String env1,String tenant2,String env2){
        if (property != null) {
            return property.replace(env2, env1).replace(tenant2, tenant1);
        } else {
            return "NA";
        }
    }

    public List<PropertyDTO> getPropertiesByIds(List<UUID> selectedIds) {
        List<Configuration> configurations = configurationRepo.findByIds(selectedIds);
        return configurations.stream()
                .map(config -> new PropertyDTO(
                        config.getId(),                  // UUID
                        config.getPropertyKey(),         // Property Key (String)
                        config.getPropertyValue(),       // Property Value (String)
                        config.getApplication(),         // Application (String)
                        config.getFieldGroup(),          // Field Group (String)
                        config.getType(),                // Type (String)
                        config.getTarget()               // Target (String)
                ))
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> envComparison(String env1, String env2) {
        // Fetch properties for the given environments
        List<? extends BaseProperties> properties1 = fetchPropertiesByEnv(env1);
        List<? extends BaseProperties> properties2 = fetchPropertiesByEnv(env2);
        // Convert properties1 to Map
        Map<String, String> env1Map = convertToMap(properties1);
        Map<String, String> env2Map = convertToMap(properties2);
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(env1Map.keySet());
        allKeys.addAll(env2Map.keySet());
        // Compare and prepare result
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : allKeys) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("propertyKey", key);
            entry.put("propertyValue1", env1Map.getOrDefault(key, "NA"));
            entry.put("propertyValue2", env2Map.getOrDefault(key, "NA"));
            entry.put("isSame", env1Map.getOrDefault(key, "NA").equalsIgnoreCase(env2Map.getOrDefault(key, "NA")));
            result.add(entry);
        }
        return result;
    }

    // Helper method to fetch properties based on environment
    private List<? extends BaseProperties> fetchPropertiesByEnv(String env) {
        switch (env) {
            case "dev1":
                return devPropertiesRepo.findAll();
            case "dev2":
                return dev2PropertiesRepo.findAll();
            case "test1":
                return testPropertiesRepo.findAll();
            case "test2":
                return test2PropertiesRepo.findAll();
            default:
                throw new IllegalArgumentException("Invalid environment: " + env);
        }
    }


    private Map<String, String> convertToMap(List<? extends BaseProperties> properties) {
        return properties.stream()
                .filter(config -> config.getPropKey() != null) // Exclude entries with null keys
                .collect(Collectors.toMap(
                        BaseProperties::getPropKey,
                        config -> config.getValue() != null ? config.getValue() : "NA",
                        (existingValue, newValue) -> existingValue // Retain the first value for duplicate keys
                ));
    }

    public CompareDTO compareProperty(String tenant1, String env1, String tenant2, String env2) {

        UUID id1 = getTenantEnvId(tenant1,env1);
        UUID id2 = getTenantEnvId(tenant2,env2);
        List<Configuration> properties1 = configurationRepo.findByTenantEnvId(id1);
        List<Configuration> properties2 = configurationRepo.findByTenantEnvId(id2);
        List<MasterConfiguration> masterConfigurationProperties = masterConfigurationRepo.findAll();
        List<PropertyComparisonDTO> comparisonResults = new ArrayList<>();
        for(MasterConfiguration masterConfig:masterConfigurationProperties) {
            String masterKey = masterConfig.getPropertyKey();
            if (masterKey.contains(TENANT_PLACEHOLDER)) {
                String propertyKey1 = masterKey.replace(TENANT_PLACEHOLDER, tenant1.toLowerCase());
                String propertyKey2 = masterKey.replace(TENANT_PLACEHOLDER, tenant2.toLowerCase());
                Configuration config1 = getMatchingKey(properties1,propertyKey1);
                Configuration config2 = getMatchingKey(properties2,propertyKey2);
                PropertyComparisonDTO comparisonDTO = createPropertyComparisonDto(masterKey,propertyKey1,config1,propertyKey2,config2);
                comparisonResults.add(comparisonDTO);
                properties1.removeIf(p -> p.getPropertyKey().equals(propertyKey1));
                properties2.removeIf(p -> p.getPropertyKey().equals(propertyKey2));
            }
        }
        List<Map<String,Object>> commonProperties=tenantEnvComparison(properties1,properties2);
        return new CompareDTO(comparisonResults,commonProperties);
    }

    private UUID getTenantEnvId(String tenant,String env){

        String uuid = tenantEnvRepo.findIdByTenantAndEnvironment(tenant.toLowerCase(), env.toLowerCase());
        if (uuid == null) {
            throw new IllegalArgumentException("No ID Found For Tenant: " + tenant + " and Environment: " + env);
        }
        return UUID.fromString(uuid);
    }

    private Configuration getMatchingKey(List<Configuration> properties,String propertyKey) {
        return properties.stream()
                .filter(p -> p.getPropertyKey().equals(propertyKey))
                .findFirst()
                .orElse(null);

    }

    private PropertyComparisonDTO createPropertyComparisonDto(String masterKey, String propertyKey1, Configuration config1, String propertyKey2, Configuration config2) {
        PropertyComparisonDTO comparisonDTO = new PropertyComparisonDTO();
        comparisonDTO.setMasterKey(masterKey);
        comparisonDTO.setPropertyKey1(propertyKey1);
        comparisonDTO.setValue1(config1 != null ? config1.getPropertyValue() : null);
        comparisonDTO.setPropertyKey2(propertyKey2);
        comparisonDTO.setValue2(config2 != null ? config2.getPropertyValue() : null);

        boolean isSame = config1 != null && config2 != null &&
                config1.getPropertyValue() != null &&
                config1.getPropertyValue().equals(config2.getPropertyValue());
        comparisonDTO.setIsSame(isSame);

        return comparisonDTO;
    }


}