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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
                List<Configuration> properties= configurationRepo.findByTenantEnvId(uuid);
                if (!properties.isEmpty()) {
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

    public void saveTenantEnvProperties(TenantEnvPropertiesDTO tenantEnvPropertiesDTO) throws ConfigurationSaveException {
        try {
            TenantEnv tenantEnv = new TenantEnv();
            tenantEnv.setTenantName(tenantEnvPropertiesDTO.getTenantName());
            tenantEnv.setTenant(tenantEnvPropertiesDTO.getTenant());
            tenantEnv.setStatus("Active");
            tenantEnv.setCreatedAt(LocalDateTime.now());
            tenantEnv.setUpdatedAt(LocalDateTime.now());
            tenantEnv.setEnvironment(tenantEnvPropertiesDTO.getEnvironment());
            tenantEnv.setTenantName(tenantEnvPropertiesDTO.getTenantName());
            TenantEnv savedTenantEnv = tenantEnvRepo.save(tenantEnv);

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
            configuration.setTenantEnv(savedTenantEnv);
            configurationRepo.save(configuration);
        } catch (Exception e) {
            throw new ConfigurationSaveException("Error while Saving configurations...!");
        }
    }

    public void deleteProperties(String uuid) {
        UUID id;
        try {
            id = UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + uuid);
        }
        Optional<Configuration> optionalConfiguration = configurationRepo.findById((id));
        if (optionalConfiguration.isPresent()) {
           configurationRepo.deleteById(id);

        } else {
            throw new EntityNotFoundException("Property not found for This ID:" + id);
        }

    }

    public void updateProperties(PropertyDTO propertyDTO) throws UpdateFailedException {
        if (propertyDTO == null||isPropertyDTOEmpty(propertyDTO)) {
            throw new IllegalArgumentException("Property cannot be null or Empty");
        }
        Optional<Configuration> properties=configurationRepo.findById(propertyDTO.getId());
        if(properties.isEmpty()){
            throw new EntityNotFoundException("No Property Found For Given ID"+propertyDTO.getId());
        }
        try {
            Configuration existingProperty = properties.get();
            existingProperty.setPropertyKey(propertyDTO.getPropertyKey());
            existingProperty.setPropertyValue(propertyDTO.getPropertyValue());
            configurationRepo.save(existingProperty);
        }
        catch (Exception e){
            throw new UpdateFailedException("Update failed for property with ID: " + propertyDTO.getId());
        }

    }
    private boolean isPropertyDTOEmpty(PropertyDTO propertyDTO) {
        return propertyDTO.getPropertyKey() == null || propertyDTO.getPropertyKey().isEmpty() ||
                propertyDTO.getPropertyValue() == null || propertyDTO.getPropertyValue().isEmpty();
    }
}
