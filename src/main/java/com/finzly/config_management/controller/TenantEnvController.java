package com.finzly.config_management.controller;

import com.finzly.config_management.DTO.EnvironmentsDTO;
import com.finzly.config_management.DTO.TenantDto;
import com.finzly.config_management.DTO.TenantEnvDto;
import com.finzly.config_management.Exception.TenantEnvCreationException;
import com.finzly.config_management.Exception.UpdateFailedException;
import com.finzly.config_management.Repository.TenantEnvRepo;
import com.finzly.config_management.model.TenantEnv;
import com.finzly.config_management.service.TenantEnvService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin
@RestController
public class TenantEnvController {

    @Autowired
    TenantEnvService tenantEnvService;

    @Autowired
    TenantEnvRepo tenantEnvRepo;

    @GetMapping("/tenants")
    public ResponseEntity<ApiResponse<List<TenantDto>>> getTenants() {
        List<TenantDto> tenants = tenantEnvService.getTenants();
        if (tenants.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>("No Tenants Found", HttpStatus.OK.value(), new ArrayList<>()));
        } else {
            return ResponseEntity.ok(new ApiResponse<>("Tenants Fetched Successfully", HttpStatus.OK.value(), tenants));
        }
    }

    @GetMapping("/environments")
    public ResponseEntity<ApiResponse<List<String>>> getEnvironments(){
        List<String> Environments = tenantEnvService.getEnvironments();
        if (Environments.isEmpty()) {
            return ResponseEntity.ok(new ApiResponse<>("No Environments Found", HttpStatus.OK.value(), new ArrayList<>()));
        } else {
            return ResponseEntity.ok(new ApiResponse<>("Environments Fetched Successfully", HttpStatus.OK.value(), Environments));
        }
    }

    @GetMapping("/tenants/{tenant}")
    public ResponseEntity<ApiResponse<EnvironmentsDTO>> getEnvironmentsByTenant(@PathVariable String tenant) {
        try {
            EnvironmentsDTO environments=tenantEnvService.getEnvironmentsForTenant(tenant);
            return ResponseEntity.ok(new ApiResponse<>("Environments Fetched successfully for given tenant", HttpStatus.OK.value(),environments));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));

        }
    }

    @PostMapping("/tenant")
    public ResponseEntity<ApiResponse<String>> saveTenant(@RequestBody TenantEnvDto tenantEnvDto) {
        try {
            tenantEnvService.saveTenantEnv(tenantEnvDto);
            return ResponseEntity.ok(new ApiResponse<>("Tenant Added Successfully..!", HttpStatus.CREATED.value()));
        } catch (TenantEnvCreationException e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


    @PostMapping("/tenant-env")
    public ResponseEntity<ApiResponse<String>> saveEnvAndAddProperties(@RequestBody TenantEnvDto tenantEnvDto) {
        try {
            tenantEnvService.saveEnvAndAddProperties(tenantEnvDto);
            return ResponseEntity.ok(new ApiResponse<>("Env And Default Properties Added Successfully..!", HttpStatus.CREATED.value()));
        } catch (TenantEnvCreationException e) {
            // Handling TenantEnvCreationException
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (UpdateFailedException e) {
            return ResponseEntity.ok(new ApiResponse<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }


}