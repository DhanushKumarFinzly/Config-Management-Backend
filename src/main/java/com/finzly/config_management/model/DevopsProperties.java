package com.finzly.config_management.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "devops_properties",
        uniqueConstraints = @UniqueConstraint(columnNames = {"prop_key", "env"})
)
public class DevopsProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    @Column(name = "prop_key", nullable = false, length = 150)
    private String propKey;

    @Column(name = "value", length = 5000)
    private String value;

    @Column(name = "env", nullable = false, length = 255)
    private String env;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "status", length = 50)
    private String status;

    // Default constructor
    public DevopsProperties() {}

    public DevopsProperties(UUID id, String propKey, String value, String env, LocalDateTime createdOn, String status) {
        this.id = id;
        this.propKey = propKey;
        this.value = value;
        this.env = env;
        this.createdOn = createdOn;
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPropKey() {
        return propKey;
    }

    public void setPropKey(String propKey) {
        this.propKey = propKey;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

