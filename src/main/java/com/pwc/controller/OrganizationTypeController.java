package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.OrganizationTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization-types")
public class OrganizationTypeController {
    
    private final OrganizationTypeService service;
    
    public OrganizationTypeController(OrganizationTypeService service) {
        this.service = service;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<OrganizationTypeDTO>> getAllOrganizationTypes() {
        return ResponseEntity.ok(service.getAllOrganizationTypes());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<OrganizationTypeDTO>> getActiveOrganizationTypes() {
        return ResponseEntity.ok(service.getActiveOrganizationTypes());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationTypeDTO> getOrganizationTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getOrganizationTypeById(id));
    }
    
    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<OrganizationTypeDTO> getOrganizationTypeBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(service.getOrganizationTypeBySlug(slug));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationTypeDTO> createOrganizationType(@RequestBody OrganizationTypeCreateDTO createDTO) {
        return ResponseEntity.ok(service.createOrganizationType(createDTO));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationTypeDTO> updateOrganizationType(
            @PathVariable Long id, 
            @RequestBody OrganizationTypeUpdateDTO updateDTO) {
        return ResponseEntity.ok(service.updateOrganizationType(id, updateDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganizationType(@PathVariable Long id) {
        service.deleteOrganizationType(id);
        return ResponseEntity.ok().build();
    }
}
