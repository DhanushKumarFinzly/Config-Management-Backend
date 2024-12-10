package com.finzly.config_management.controller;


import com.finzly.config_management.model.MasterConfiguration;
import com.finzly.config_management.service.MasterConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin
@RestController
@RequestMapping(value = "/api")
public class MasterConfigurationController {

    @Autowired
    MasterConfigurationService masterConfigurationService;



}
