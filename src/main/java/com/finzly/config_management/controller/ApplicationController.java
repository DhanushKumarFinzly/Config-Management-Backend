package com.finzly.config_management.controller;

import com.finzly.config_management.model.Application;
import com.finzly.config_management.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "/api")
public class ApplicationController {

    @Autowired
    ApplicationService applicationService;

    @GetMapping("/application")
    public ResponseEntity<ApiResponse<List<String>>> getAppName(){
        List<String> application=applicationService.getAppName();
        return ResponseEntity.ok(new ApiResponse<>("Successfully Fetched all the application", HttpStatus.OK.value(),application));
    }

}
