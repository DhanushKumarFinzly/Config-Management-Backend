package com.finzly.config_management.service;

import com.finzly.config_management.DTO.EnvironmentsDTO;
import com.finzly.config_management.DTO.TenantDto;
import com.finzly.config_management.DTO.TenantEnvDto;
import com.finzly.config_management.Exception.TenantEnvCreationException;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.TenantEnv;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TenantEnvService {

    @Autowired
    TenantEnvRepo tenantEnvRepo;

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
        } catch (Exception e) {
            throw new TenantEnvCreationException("Error while adding tenant environment data: ");
        }

    }


}