package com.finzly.config_management.service;

import com.finzly.config_management.DTO.EnvironmentsDTO;
import com.finzly.config_management.DTO.TenantDto;
import com.finzly.config_management.DTO.TenantEnvDto;
import com.finzly.config_management.Exception.TenantEnvCreationException;
import com.finzly.config_management.Exception.UpdateFailedException;
import com.finzly.config_management.Repository.ConfigurationRepo;
import com.finzly.config_management.Repository.DevopsPropertyRepo;
import com.finzly.config_management.Repository.MasterConfigurationRepo;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.Configuration;
import com.finzly.config_management.model.DevopsProperties;
import com.finzly.config_management.model.MasterConfiguration;
import com.finzly.config_management.model.TenantEnv;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TenantEnvService {

    @Autowired
    TenantEnvRepo tenantEnvRepo;

    @Autowired
    ConfigurationRepo configurationRepo;

    @Autowired
    MasterConfigurationRepo masterConfigurationRepo;

    @Autowired
    DevopsPropertyRepo devopsPropertyRepo;


    public List<TenantDto> getTenants() {
        List<Object[]> tenants = tenantEnvRepo.findDistinctTenantsAndTenantName();
        List<TenantDto> tenantList = new ArrayList<>();
        tenants.forEach(row -> tenantList.add(new TenantDto((String) row[0], (String) row[1],(String) row[2])));
        return tenantList;
    }

    public List<String> getEnvironments() {
        return tenantEnvRepo.findDistinctEnvironments();
    }

    public EnvironmentsDTO getEnvironmentsForTenant(String tenant) {
        List<String> existingTenantName= tenantEnvRepo.findTenantNameByTenant(tenant);
        List<String> environments=tenantEnvRepo.findEnvironmentsByTenant(tenant);
        if(existingTenantName.isEmpty()){
            throw new EntityNotFoundException("No tenantName Found For this Tenant"+tenant);
        }
        else if(environments.isEmpty()){
            throw new EntityNotFoundException("No Environments Found For this Tenant"+tenant);
        }
        else {
            String tenantName=existingTenantName.get(0);
            return new EnvironmentsDTO(tenantName, environments);
        }
    }

    public void saveTenantEnv(TenantEnvDto tenantEnvDto) throws TenantEnvCreationException {
        try {
            TenantEnv tenantEnv = new TenantEnv();
            tenantEnv.setTenant(tenantEnvDto.getTenant());
            tenantEnv.setTenantName(tenantEnvDto.getTenantName());
            tenantEnv.setEnvironment(tenantEnvDto.getEnvironment());
            tenantEnv.setStatus("Active");
            tenantEnv.setCreatedAt(LocalDateTime.now());
            tenantEnv.setUpdatedAt(LocalDateTime.now());
            tenantEnvRepo.save(tenantEnv);
            addPropertiesForNewTenant(tenantEnvDto.getTenant(), tenantEnvDto.getEnvironment());
        } catch (Exception e) {
            throw new TenantEnvCreationException("Error while adding tenant environment data: ");
        }
    }

    public void addPropertiesForNewTenant(String newTenant, String newEnv) throws UpdateFailedException {
        try {
            List<MasterConfiguration> masterConfigurations = masterConfigurationRepo.findAll();
            String tenantEnvId = tenantEnvRepo.findIdByTenantAndEnvironment(newTenant, newEnv);
            List<Configuration> configurations = new ArrayList<>();
            for (MasterConfiguration masterConfig : masterConfigurations) {
                String updatedPropertyKey = masterConfig.getPropertyKey().replace("TENANT_NAME_ID", newTenant);
                String updatedPropertyValue = masterConfig.getPropertyValue();
                if (updatedPropertyValue != null) {
                    if(updatedPropertyValue.contains("${env}")){
                        updatedPropertyValue = updatedPropertyValue.replace("${env}", newEnv);
                    }
                    if(updatedPropertyValue.contains("TENANT_NAME_ID")){
                        updatedPropertyValue = updatedPropertyValue.replace("TENANT_NAME_ID", newTenant);
                    }
                    if (updatedPropertyValue.startsWith("runtime")) {
                        updatedPropertyValue = "NA";
                    }
                    if (updatedPropertyValue.startsWith("devops.")) {
                        String devopsKey = updatedPropertyValue.substring("devops.".length());

                        Optional<DevopsProperties> devopsProperty = devopsPropertyRepo.findByPropKeyAndEnv(devopsKey, newEnv);
                        if (devopsProperty.isPresent()) {
                            updatedPropertyValue = devopsProperty.get().getValue();

                        } else {
                            updatedPropertyValue = "DEVOPS_INPUT";
                        }
                    }
                } else {
                    updatedPropertyValue = "NA";
                }
                Configuration configuration = new Configuration(
                        updatedPropertyKey,
                        masterConfig.getFieldGroup(),
                        masterConfig.getApplication(),
                        updatedPropertyValue,
                        masterConfig.getTarget(),
                        masterConfig.getType(),
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        masterConfig.getStatus(),
                        masterConfig.getProduct(),
                        masterConfig.getAppId(),
                        masterConfig.getIsSecureString(),
                        UUID.fromString(tenantEnvId)
                );
                configurations.add(configuration);
            }
            configurationRepo.saveAll(configurations);
            System.out.println("Saved configurations: " + configurations.size());
        } catch (Exception e) {
            throw new UpdateFailedException("Failed to add New properties for New tenant: " + newTenant);
        }
    }
     

    


}