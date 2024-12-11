package com.finzly.config_management.service;

import com.finzly.config_management.Repository.ApplicationRepo;
import com.finzly.config_management.model.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    @Autowired
    ApplicationRepo applicationRepo;

    public List<String> getAppName(){
        return applicationRepo.findAll()
                .stream()
                .map(Application::getAppName)
                .collect(Collectors.toList());
    }
}
