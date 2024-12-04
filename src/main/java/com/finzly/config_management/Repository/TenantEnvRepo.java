package com.finzly.config_management.Repository;

import com.finzly.config_management.DTO.TenantDto;
import com.finzly.config_management.model.TenantEnv;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TenantEnvRepo extends JpaRepository<TenantEnv, UUID> {
    @Query(value = "SELECT DISTINCT tenant, tenant_name,status FROM Tenant_env", nativeQuery = true)
    List<Object[]> findDistinctTenantsAndTenantName();

    @Query("SELECT DISTINCT environment FROM TenantEnv")
    List<String> findDistinctEnvironments();

    @Query("SELECT environment FROM TenantEnv WHERE tenant = :tenant")
    List<String> findEnvironmentsByTenant(String tenant);

    @Query("SELECT  tenantName FROM TenantEnv WHERE tenant = :tenant")
    List<String>  findTenantNameByTenant(String tenant);

    @Query("SELECT id FROM TenantEnv  WHERE tenant = :tenant AND environment = :environment")
    String findIdByTenantAndEnvironment(String tenant,String environment);



}