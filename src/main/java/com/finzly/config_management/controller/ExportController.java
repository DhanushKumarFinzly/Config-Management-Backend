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
            writer.printf("INSERT INTO %s (property_key, property_value) VALUES ('%s', '%s');%n",
                    tableName, property.getPropertyKey(), property.getPropertyValue());
        }

        writer.close();
    }

}