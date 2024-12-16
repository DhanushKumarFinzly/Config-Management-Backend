package com.finzly.config_management.Repository;

import com.finzly.config_management.model.TestProperties;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestPropertiesRepo extends JpaRepository<TestProperties,String> {
}
