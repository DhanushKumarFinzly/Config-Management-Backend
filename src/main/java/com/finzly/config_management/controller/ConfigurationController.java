package com.finzly.config_management.controller;

import com.finzly.config_management.DTO.CompareDTO;
import com.finzly.config_management.DTO.InterChangeDTO;
import com.finzly.config_management.DTO.PropertyDTO;
import com.finzly.config_management.DTO.TenantEnvPropertiesDTO;
import com.finzly.config_management.Exception.ConfigurationSaveException;
import com.finzly.config_management.Exception.UpdateFailedException;
import com.finzly.config_management.service.ConfigurationService;
import com.finzly.config_management.Exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name="Configuration")
public class ConfigurationController {

    @Autowired
    ConfigurationService configurationService;

    @GetMapping("properties/{tenant}/{environment}")
    public ResponseEntity<ApiResponse<List<PropertyDTO>>> getProperty(
            @PathVariable String tenant,
            @PathVariable String environment)
            throws DataNotFoundException {
        try {
            List<PropertyDTO> properties=configurationService.getProperty(tenant, environment);
            return ResponseEntity.ok(new ApiResponse<>("Property found successfully!", HttpStatus.OK.value(),properties));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value(), Collections.emptyList()));
        }
    }

    @PostMapping("/properties")
    public ResponseEntity<ApiResponse<String>> saveTenantEnvProperties(
            @RequestBody TenantEnvPropertiesDTO tenantEnvPropertiesDTO)
            throws ConfigurationSaveException {
        try{
            configurationService.saveTenantEnvProperties(tenantEnvPropertiesDTO);
            return ResponseEntity.ok(new ApiResponse<>("Configurations Saved successfully!", HttpStatus.OK.value()));
        }
        catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @DeleteMapping("/properties/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProperty(@PathVariable String id) {
        try {
            configurationService.deleteProperties(id);
            return ResponseEntity.ok(new ApiResponse<>("Property deleted successfully!", HttpStatus.OK.value()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @PutMapping("/properties")
    public ResponseEntity<ApiResponse<String>> updateProperties(
            @RequestBody PropertyDTO propertyDTO)
            throws UpdateFailedException {
        try {
            configurationService.updateProperties(propertyDTO);
            return ResponseEntity.ok(new ApiResponse<>("Property Updated SuccessFully...!", HttpStatus.CREATED.value()));
        }catch (IllegalArgumentException e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        catch (EntityNotFoundException e){
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("properties/compare/{tenant1}/{environment1}/{tenant2}/{environment2}")
    public ResponseEntity<ApiResponse<CompareDTO>> tenantEnvComparison(
            @PathVariable String tenant1,
            @PathVariable String environment1,
            @PathVariable String tenant2,
            @PathVariable String environment2
    )
    {
        try {
            CompareDTO result = configurationService.compareProperty(tenant1, environment1, tenant2, environment2);
            return ResponseEntity.ok(new ApiResponse<>("Property Fetched Successfully!",HttpStatus.OK.value(), result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @GetMapping("properties/compare-env/{env1}/{env2}")
    public ResponseEntity<ApiResponse<List<Map<String,Object>>>> tenantEnvComparison(@PathVariable String env1,@PathVariable String env2) {
        try {
            List<Map<String,Object>> result = configurationService.envComparison(env1,env2);
            return ResponseEntity.ok(new ApiResponse<>("Properties Successfully Compared!",HttpStatus.OK.value(), result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }




    @PutMapping("properties/inter-change")
    public ResponseEntity<ApiResponse<String>> changeProperty(@RequestBody InterChangeDTO interChangeDTO) {
        try {
            configurationService.changeProperty(interChangeDTO);
            return ResponseEntity.ok(
                    new ApiResponse<>("Property Updated Successfully!", HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(
                    new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }


    @PutMapping("/clone/{tenant1}/{env1}/{tenant2}/{env2}")
    public ResponseEntity<ApiResponse<String>> clonePropertyForNewTenant(@PathVariable String tenant1,@PathVariable String env1,@PathVariable String tenant2,@PathVariable String env2){
        try{
            configurationService.clonePropertyForNewTenant(tenant1,env1,tenant2,env2);
            return ResponseEntity.ok(new ApiResponse<>("Properties Cloned Sucessfully...!", HttpStatus.OK.value()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }




}