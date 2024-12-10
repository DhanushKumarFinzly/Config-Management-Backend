package com.finzly.config_management.Repository;

import com.finzly.config_management.model.DevopsProperties;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DevopsPropertyRepo extends JpaRepository<DevopsProperties, UUID> {
    Optional<DevopsProperties> findByPropKeyAndEnv(String devopsKey, String newEnv);

}
