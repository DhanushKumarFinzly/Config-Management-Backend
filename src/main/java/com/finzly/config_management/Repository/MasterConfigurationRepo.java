package com.finzly.config_management.Repository;

import com.finzly.config_management.model.MasterConfiguration;
import org.apache.catalina.authenticator.jaspic.PersistentProviderRegistrations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterConfigurationRepo extends JpaRepository<MasterConfiguration, String> {

}
