package com.finzly.config_management.controller;

import com.finzly.config_management.Repository.DevopsPropertyRepo;
import com.finzly.config_management.model.DevopsProperties;
import com.finzly.config_management.service.DevopsPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api")
public class DevopsPropertyController {

    @Autowired
    DevopsPropertyService devopsPropertyService;
    @Autowired
    DevopsPropertyRepo devopsPropertyRepo;

    @GetMapping("get")
    public List<DevopsProperties> get(){
        return  devopsPropertyRepo.findAll();
    }


}
