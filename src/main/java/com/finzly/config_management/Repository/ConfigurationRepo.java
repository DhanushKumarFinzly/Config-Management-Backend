package com.finzly.config_management.Repository;

import com.finzly.config_management.model.Configuration;
import com.finzly.config_management.model.TenantEnv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConfigurationRepo extends JpaRepository<Configuration, UUID> {


    @Query("SELECT c FROM Configuration c WHERE c.tenantEnv = :uuid")
    List<Configuration> findByTenantEnvId(UUID uuid);

    boolean existsByPropertyKeyAndTenantEnv(String propertyKey, UUID tenantEnv);

}