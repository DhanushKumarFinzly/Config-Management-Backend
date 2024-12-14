package com.finzly.config_management.controller;


import com.finzly.config_management.DTO.PropertyDTO;
import com.finzly.config_management.Exception.DataNotFoundException;
import com.finzly.config_management.service.ConfigurationService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class ExportController {

    @Autowired
    ConfigurationService configurationService;

    @GetMapping("/exportProperties/{tenant}/{environment}")
    public void exportToSql(
            @PathVariable String tenant,
            @PathVariable String environment,
            HttpServletResponse response) throws IOException, DataNotFoundException {
        response.setContentType("text/sql");
        response.setHeader("Content-Disposition", "attachment; filename=" + tenant + "_" + environment + "_properties.sql");

        PrintWriter writer = response.getWriter();

        String tableName = "tenant_properties";
        List<PropertyDTO> properties = configurationService.getProperty(tenant, environment);

        writer.println("-- SQL script to insert tenant properties");
        for (PropertyDTO property : properties) {
            writer.printf("INSERT INTO %s (property_key, property_value, application, field_group, type, target) " +
                            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');%n",
                    tableName,
                    property.getPropertyKey(),
                    property.getPropertyValue(),
                    property.getApplication(),
                    property.getFieldGroup(),
                    property.getType(),
                    property.getTarget());
        }


        writer.close();
    }

    @PostMapping("/exportSelectedProperties/{tenant}/{environment}")
    public void exportSelectedProperties(
            @PathVariable String tenant,
            @PathVariable String environment,
            @RequestBody List<String> request,
            HttpServletResponse response) throws IOException, DataNotFoundException {

        if (request == null || request.isEmpty()) {
            throw new DataNotFoundException("No properties selected for export.");
        }

        List<UUID> uuidList = request.stream()
                .map(UUID::fromString)  // Convert each String to UUID
                .collect(Collectors.toList());

        for(UUID id:uuidList){
            System.out.println(id);
        }
        System.out.println();
        response.setContentType("text/sql");
        response.setHeader("Content-Disposition", "attachment; filename=" + tenant + "_" + environment + "_selected_properties.sql");

        PrintWriter writer = response.getWriter();

        String tableName = "tenant_properties";
        List<PropertyDTO> selectedProperties = configurationService.getPropertiesByIds(uuidList);

        writer.println("-- SQL script to insert selected tenant properties");
        for (PropertyDTO property : selectedProperties) {
            writer.printf("INSERT INTO %s (property_key, property_value, application, field_group, type, target) " +
                            "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');%n",
                    tableName,
                    property.getPropertyKey(),
                    property.getPropertyValue(),
                    property.getApplication(),
                    property.getFieldGroup(),
                    property.getType(),
                    property.getTarget());
        }


        writer.close();
    }

}