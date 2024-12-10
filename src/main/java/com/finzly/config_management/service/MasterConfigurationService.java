package com.finzly.config_management.service;

import com.finzly.config_management.Repository.ConfigurationRepo;
import com.finzly.config_management.Repository.MasterConfigurationRepo;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.Configuration;
import com.finzly.config_management.model.MasterConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class MasterConfigurationService {

    @Autowired
    MasterConfigurationRepo masterConfigurationRepo;

    @Autowired
    TenantEnvRepo tenantEnvRepo;

    @Autowired
    ConfigurationRepo configurationRepo;


}
