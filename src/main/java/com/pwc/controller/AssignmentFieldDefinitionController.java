package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.AssignmentFieldDefinitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/field-definitions")
public class AssignmentFieldDefinitionController {
    
    private final AssignmentFieldDefinitionService service;
    
    public AssignmentFieldDefinitionController(AssignmentFieldDefinitionService service) {
        this.service = service;
    }
    
    @GetMapping
    public ResponseEntity<List<AssignmentFieldDefinitionDTO>> getFieldDefinitionsByOrgType(
            @RequestParam Long orgTypeId,
            @RequestParam(defaultValue = "false") boolean activeOnly) {
        if (activeOnly) {
            return ResponseEntity.ok(service.getActiveFieldDefinitionsByOrgTypeId(orgTypeId));
        }
        return ResponseEntity.ok(service.getFieldDefinitionsByOrgTypeId(orgTypeId));
    }
    
    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<List<AssignmentFieldDefinitionDTO>> getFieldDefinitionsByOrgTypeSlug(
            @PathVariable String slug) {
        return ResponseEntity.ok(service.getActiveFieldDefinitionsByOrgTypeSlug(slug));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentFieldDefinitionDTO> getFieldDefinitionById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getFieldDefinitionById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignmentFieldDefinitionDTO> createFieldDefinition(
            @RequestBody AssignmentFieldDefinitionCreateDTO createDTO) {
        return ResponseEntity.ok(service.createFieldDefinition(createDTO));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssignmentFieldDefinitionDTO> updateFieldDefinition(
            @PathVariable Long id,
            @RequestBody AssignmentFieldDefinitionUpdateDTO updateDTO) {
        return ResponseEntity.ok(service.updateFieldDefinition(id, updateDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFieldDefinition(@PathVariable Long id) {
        service.deleteFieldDefinition(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}/hard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> hardDeleteFieldDefinition(@PathVariable Long id) {
        service.hardDeleteFieldDefinition(id);
        return ResponseEntity.ok().build();
    }
}
