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
            List<Configuration> properties1, List<Configuration> properties2) {

        // Convert properties1 to Map with null handling
        Map<String, String> tenant1Map = properties1.stream()
                .collect(Collectors.toMap(
                        config -> config.getPropertyKey() != null ? config.getPropertyKey() : "NA",
                        config -> config.getPropertyValue() != null ? config.getPropertyValue() : "NA",
                        (existingValue, newValue) -> existingValue
                ));

        // Convert properties2 to Map with null handling
        Map<String, String> tenant2Map = properties2.stream()
                .collect(Collectors.toMap(
                        config -> config.getPropertyKey() != null ? config.getPropertyKey() : "NA",
                        config -> config.getPropertyValue() != null ? config.getPropertyValue() : "NA",
                        (existingValue, newValue) -> existingValue
                ));

        // Combine all keys from both maps
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(tenant1Map.keySet());
        allKeys.addAll(tenant2Map.keySet());

        // Prepare the result list
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : allKeys) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("propertyKey", key);
            entry.put("PropertyValue1", tenant1Map.getOrDefault(key, "NA")); // Replace null with "NA"
            entry.put("PropertyValue2", tenant2Map.getOrDefault(key, "NA")); // Replace null with "NA"

            // Compare values and mark as same or different
            if (tenant1Map.getOrDefault(key, "NA").equalsIgnoreCase(tenant2Map.getOrDefault(key, "NA"))) {
                entry.put("isSame", true);
            } else {
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



//    public CompareDTO tenantEnvKeyComparison(String tenant1, String environment1,
//            String tenant2, String environment2) {
//
//        String uuid1 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant1.toLowerCase(), environment1.toLowerCase());
//        String uuid2 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant2.toLowerCase(), environment2.toLowerCase());
//
//        if (uuid1 == null) {
//            throw new IllegalArgumentException("No ID Found For Tenant: " + tenant1 + " and Environment: " + environment1);
//        }
//        if (uuid2 == null) {
//            throw new IllegalArgumentException("No ID Found For Tenant: " + tenant2 + " and Environment: " + environment2);
//        }
//
//        UUID id1 = UUID.fromString(uuid1);
//        UUID id2 = UUID.fromString(uuid2);
//
//        List<Configuration> properties1 = configurationRepo.findByTenantEnvId(id1);
//        List<Configuration> properties2 = configurationRepo.findByTenantEnvId(id2);
//
//        List<Map<String, Object>> result = new ArrayList<>();
//
//        // Iterate through properties1
//        Iterator<Configuration> iterator1 = properties1.iterator();
//        while (iterator1.hasNext()) {
//            Configuration config1 = iterator1.next();
//            String key1 = config1.getPropertyKey().toLowerCase();
//
//            // Nested iteration for properties2
//            Iterator<Configuration> iterator2 = properties2.iterator();
//            while (iterator2.hasNext()) {
//                Configuration config2 = iterator2.next();
//                String key2 = config2.getPropertyKey().toLowerCase();
//
//                if (key1.contains(tenant1.toLowerCase()) && key2.contains(tenant2.toLowerCase())) {
//                    // Normalize keys by replacing tenant names
//                    String normalizedKey1 = key1.replace(tenant1.toLowerCase(), "{tenant}");
//                    String normalizedKey2 = key2.replace(tenant2.toLowerCase(), "{tenant}");
//
//                    // Check if normalized keys match but values differ
//                    if (normalizedKey1.equals(normalizedKey2) &&
//                            !config1.getPropertyValue().equals(config2.getPropertyValue())) {
//
//                        // Create entries for differing properties
//                        Map<String, Object> list1Entry = new HashMap<>();
//                        list1Entry.put("propertyKey", key1);
//                        list1Entry.put("propertyValue", config1.getPropertyValue());
//
//                        Map<String, Object> list2Entry = new HashMap<>();
//                        list2Entry.put("propertyKey", key2);
//                        list2Entry.put("propertyValue", config2.getPropertyValue());
//
//                        // Add entries to result
//                        result.add(list1Entry);
//                        result.add(list2Entry);
//
//                        // Remove matched entries from both lists
//                        iterator1.remove();
//                        iterator2.remove();
//
//                        // Exit inner loop as key1 is matched
//                        break;
//                    }
//                }
//            }
//        }
//
//        // Compare remaining unmatched entries
//        List<Map<String, Object>> result1 = tenantEnvComparison(properties1, properties2);
//
//        return new CompareDTO(result, result1);
//    }


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
        Map<String, String> env1Map = properties1.stream()
                .collect(Collectors.toMap(
                        config -> config.getPropKey() != null ? config.getPropKey() : "NA",
                        config -> config.getValue() != null ? config.getValue() : "NA",
                        (existingValue, newValue) -> existingValue
                ));

        // Convert properties2 to Map
        Map<String, String> env2Map = properties2.stream()
                .collect(Collectors.toMap(
                        config -> config.getPropKey() != null ? config.getPropKey() : "NA",
                        config -> config.getValue() != null ? config.getValue() : "NA",
                        (existingValue, newValue) -> existingValue
                ));

        // Combine all keys from both maps
        Set<String> allKeys = new HashSet<>();
        allKeys.addAll(env1Map.keySet());
        allKeys.addAll(env2Map.keySet());

        // Compare and prepare result
        List<Map<String, Object>> result = new ArrayList<>();
        for (String key : allKeys) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("propertyKey", key);
            entry.put("PropertyValue1", env1Map.getOrDefault(key, "NA"));
            entry.put("PropertyValue2", env2Map.getOrDefault(key, "NA"));
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

    public CompareDTO compareProperty(String tenant1, String env1, String tenant2, String env2) {
        String uuid1 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant1.toLowerCase(), env1.toLowerCase());
        String uuid2 = tenantEnvRepo.findIdByTenantAndEnvironment(tenant2.toLowerCase(), env2.toLowerCase());

        if (uuid1 == null) {
            throw new IllegalArgumentException("No ID Found For Tenant: " + tenant1 + " and Environment: " + env1);
        }
        if (uuid2 == null) {
            throw new IllegalArgumentException("No ID Found For Tenant: " + tenant2 + " and Environment: " + env2);
        }

        UUID id1 = UUID.fromString(uuid1);
        UUID id2 = UUID.fromString(uuid2);

        List<Configuration> properties1 = configurationRepo.findByTenantEnvId(id1);
        List<Configuration> properties2 = configurationRepo.findByTenantEnvId(id2);
        List<MasterConfiguration> masterConfigurationProperties = masterConfigurationRepo.findAll();

        List<PropertyComparisonDTO> comparisonResults = new ArrayList<>();

        Iterator<MasterConfiguration> iterator = masterConfigurationProperties.iterator();

        while (iterator.hasNext()) {
            MasterConfiguration masterConfig = iterator.next();
            String masterKey = masterConfig.getPropertyKey();

            if (masterKey.contains("TENANT_NAME_ID")) {
                // Replace TENANT_NAME_ID with tenant1 and tenant2
                String propertyKey1 = masterKey.replace("TENANT_NAME_ID", tenant1.toLowerCase());
                String propertyKey2 = masterKey.replace("TENANT_NAME_ID", tenant2.toLowerCase());

                // Search for keys in properties1 and properties2
                Configuration config1 = properties1.stream()
                        .filter(p -> p.getPropertyKey().equals(propertyKey1))
                        .findFirst()
                        .orElse(null);

                Configuration config2 = properties2.stream()
                        .filter(p -> p.getPropertyKey().equals(propertyKey2))
                        .findFirst()
                        .orElse(null);

                // Extract values
                String value1 = (config1 != null) ? config1.getPropertyValue() : "NA";
                String value2 = (config2 != null) ? config2.getPropertyValue() : "NA";

                // Add result to comparison DTO
                PropertyComparisonDTO comparisonDTO = new PropertyComparisonDTO();
                comparisonDTO.setMasterKey(masterKey);
                comparisonDTO.setPropertyKey1(propertyKey1);
                comparisonDTO.setValue1(value1);
                comparisonDTO.setPropertyKey2(propertyKey2);
                comparisonDTO.setValue2(value2);
                comparisonDTO.setIsSame(value1.equals(value2)); // Compare values and set isSame

                comparisonResults.add(comparisonDTO);

                // Remove matched keys from properties1 and properties2
                properties1.removeIf(p -> p.getPropertyKey().equals(propertyKey1));
                properties2.removeIf(p -> p.getPropertyKey().equals(propertyKey2));
            }
        }

        CompareDTO compareDTO = new CompareDTO();
        compareDTO.setTenantBasedProperties(comparisonResults);
        System.out.println(properties1.size());
        System.out.println(properties2.size());
        List<Map<String,Object>> commonProperties=tenantEnvComparison(properties1,properties2);
        compareDTO.setCommonProperties(commonProperties);
        return compareDTO;
    }




}