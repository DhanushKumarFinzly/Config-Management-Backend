package com.finzly.config_management.controller;

import com.finzly.config_management.DTO.PropertyDTO;
import com.finzly.config_management.Repository.ConfigurationRepo;
import com.finzly.config_management.service.ConfigurationService;
import com.finzly.config_management.Exception.DataNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api")
public class ConfigurationController {


    @Autowired
    ConfigurationService configurationService;

    @Autowired
    ConfigurationRepo configurationRepo;

    @GetMapping("/{tenant}/{environment}")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> getProperty(@PathVariable String tenant, @PathVariable String environment) throws DataNotFoundException {
       try {
           List<PropertyDTO> properties=configurationService.getProperty(tenant, environment);
           return ResponseEntity.ok(new ApiResponse<>("Property found successfully!", HttpStatus.OK.value(),properties));
       } catch (Exception e) {
          return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value()));
       }

    }



}
