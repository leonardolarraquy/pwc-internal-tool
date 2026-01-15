package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.OrganizationDetailService;
import com.pwc.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organization-details")
public class OrganizationDetailController {
    
    private final OrganizationDetailService organizationDetailService;
    private final AssignmentService assignmentService;
    
    public OrganizationDetailController(
            OrganizationDetailService organizationDetailService,
            AssignmentService assignmentService) {
        this.organizationDetailService = organizationDetailService;
        this.assignmentService = assignmentService;
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<OrganizationDetailDTO>> getAllOrganizationDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String organizationTypeFilter) {
        
        PageResponse<OrganizationDetailDTO> response = organizationDetailService.getAllOrganizationDetails(page, size, sortBy, sortDir, search, organizationTypeFilter);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/organization-types")
    public ResponseEntity<Map<String, Object>> getOrganizationTypes() {
        List<String> types = organizationDetailService.getDistinctOrganizationTypes();
        Map<String, Object> response = new HashMap<>();
        response.put("types", types);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDetailDTO> getOrganizationDetailById(@PathVariable Long id) {
        OrganizationDetailDTO organizationDetail = organizationDetailService.getOrganizationDetailById(id);
        return ResponseEntity.ok(organizationDetail);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDetailDTO> createOrganizationDetail(@Valid @RequestBody OrganizationDetailCreateDTO createDTO) {
        OrganizationDetailDTO organizationDetail = organizationDetailService.createOrganizationDetail(createDTO);
        return ResponseEntity.ok(organizationDetail);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDetailDTO> updateOrganizationDetail(@PathVariable Long id, 
                                                                          @Valid @RequestBody OrganizationDetailUpdateDTO updateDTO) {
        OrganizationDetailDTO organizationDetail = organizationDetailService.updateOrganizationDetail(id, updateDTO);
        return ResponseEntity.ok(organizationDetail);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganizationDetail(@PathVariable Long id) {
        organizationDetailService.deleteOrganizationDetail(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importOrganizationDetails(@RequestParam("file") MultipartFile file) {
        int importedCount = organizationDetailService.importOrganizationDetailsFromCsv(file);
        Map<String, Object> response = new HashMap<>();
        response.put("imported", importedCount);
        response.put("message", "Successfully imported " + importedCount + " organization details");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AssignmentDTO>> getOrganizationAssignments(@PathVariable Long id) {
        List<AssignmentDTO> assignments = assignmentService.getAssignmentsByOrgDetailId(id);
        return ResponseEntity.ok(assignments);
    }
}
