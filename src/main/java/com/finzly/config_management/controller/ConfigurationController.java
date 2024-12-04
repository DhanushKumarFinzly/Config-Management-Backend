package com.finzly.config_management.controller;

import com.finzly.config_management.DTO.PropertyDTO;
import com.finzly.config_management.DTO.TenantEnvPropertiesDTO;
import com.finzly.config_management.Exception.ConfigurationSaveException;
import com.finzly.config_management.Exception.UpdateFailedException;
import com.finzly.config_management.Repository.ConfigurationRepo;
import com.finzly.config_management.service.ConfigurationService;
import com.finzly.config_management.Exception.DataNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/api")
public class ConfigurationController {
    @Autowired
    ConfigurationService configurationService;
    @GetMapping("/{tenant}/{environment}")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> getProperty(@PathVariable String tenant, @PathVariable String environment) throws DataNotFoundException {
        try {
            List<PropertyDTO> properties=configurationService.getProperty(tenant, environment);
            return ResponseEntity.ok(new ApiResponse<>("Property found successfully!", HttpStatus.OK.value(),properties));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value(), Collections.emptyList()));
        }
    }
    @PostMapping("/tenant-env-configuration")
    public ResponseEntity<ApiResponse<String>> saveTenantEnvProperties(@RequestBody TenantEnvPropertiesDTO tenantEnvPropertiesDTO) throws ConfigurationSaveException {
        try{
            configurationService.saveTenantEnvProperties(tenantEnvPropertiesDTO);
            return ResponseEntity.ok(new ApiResponse<>("Configurations Saved successfully!", HttpStatus.OK.value()));
        }
        catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProperties(@PathVariable String id){
        try {
            configurationService.deleteProperties(id);
            return ResponseEntity.ok(new ApiResponse<>("Property Deleted SuccessFully...!", HttpStatus.OK.value()));
        }
        catch (Exception e) {
            String message = e.getMessage();
            HttpStatus status;
            if (message.contains("Invalid UUID format")) {
                status = HttpStatus.BAD_REQUEST;
            } else{
                status = HttpStatus.NOT_FOUND;
            }
            return ResponseEntity.status(status).body(new ApiResponse<>(message, status.value()));
        }
    }

    @PutMapping
    public ResponseEntity<ApiResponse<String>> updateProperties(@RequestBody PropertyDTO propertyDTO) throws UpdateFailedException {
        try {
            configurationService.updateProperties(propertyDTO);
            return ResponseEntity.ok(new ApiResponse<>("Property Updated SuccessFully...!", HttpStatus.CREATED.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        catch (EntityNotFoundException e){
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("{tenant1}/{environment1}/{tenant2}/{environment2}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> tenantEnvComparison(
            @PathVariable String tenant1,
            @PathVariable String environment1,
            @PathVariable String tenant2,
            @PathVariable String environment2
    ) {
        try {
            List<Map<String, Object>> result = configurationService.tenantEnvComparison(
                    tenant1, environment1, tenant2, environment2
            );

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            "Property Fetched Successfully!",
                            HttpStatus.OK.value(),
                            result
                    )
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST.value()
                    )
            );
        }
    }


    @PutMapping("{tenant}/{environment}/{propertyKey}/{newValue}")
    public ResponseEntity<ApiResponse<String>> changeProperty(
            @PathVariable String tenant,
            @PathVariable String environment,
            @PathVariable String propertyKey,
            @PathVariable String newValue) {
        try {
            configurationService.changeProperty(tenant, environment, propertyKey, newValue);
            return ResponseEntity.ok(
                    new ApiResponse<>("Property Updated Successfully!", HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(
                    new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }


}