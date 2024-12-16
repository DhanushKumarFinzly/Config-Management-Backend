package com.finzly.config_management.Repository;

import com.finzly.config_management.model.DevProperties;
import com.finzly.config_management.model.DevopsProperties;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DevPropertiesRepo extends JpaRepository<DevProperties,String> {
}
