package com.finzly.config_management.service;

import com.finzly.config_management.DTO.TenantDto;
import com.finzly.config_management.DTO.TenantEnvDto;
import com.finzly.config_management.Exception.TenantEnvCreationException;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.TenantEnv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


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
    public List<String> getEnvironmentsForTenant(String tenant) {
        return tenantEnvRepo.findEnvironmentsByTenant(tenant);
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
