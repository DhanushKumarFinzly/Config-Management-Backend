package com.finzly.config_management.service;

import com.finzly.config_management.Repository.DevopsPropertyRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DevopsPropertyService {

    @Autowired
    DevopsPropertyRepo devopsPropertyRepo;


}
