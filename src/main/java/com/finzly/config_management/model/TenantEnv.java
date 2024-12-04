package com.finzly.config_management.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "Tenant_env",
        uniqueConstraints = @UniqueConstraint(columnNames = {"tenant", "environment"})
)
public class TenantEnv {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    @Column(name = "tenant", nullable = false)
    private String tenant;

    @Column(name = "environment", nullable = false)
    private String environment;

    @Column(name = "tenant_name")
    private String tenantName;



    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }



    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }


    // Default constructor
    public TenantEnv() {}

    public TenantEnv(UUID id, String tenant, String environment, String tenantName, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.tenant = tenant;
        this.environment = environment;
        this.tenantName = tenantName;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public TenantEnv(String tenant, String environment, String tenantName, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.tenant = tenant;
        this.environment = environment;
        this.tenantName = tenantName;

        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


}