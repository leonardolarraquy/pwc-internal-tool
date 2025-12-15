package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.CompanyAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-assignments")
public class CompanyAssignmentController {
    
    private final CompanyAssignmentService companyAssignmentService;
    
    public CompanyAssignmentController(CompanyAssignmentService companyAssignmentService) {
        this.companyAssignmentService = companyAssignmentService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CompanyAssignmentDTO>> getAllCompanyAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        PageResponse<CompanyAssignmentDTO> response = companyAssignmentService.getAllCompanyAssignments(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyAssignmentDTO> getCompanyAssignmentById(@PathVariable Long id) {
        CompanyAssignmentDTO assignment = companyAssignmentService.getCompanyAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }
    
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<CompanyAssignmentDTO>> getCompanyAssignmentsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        PageResponse<CompanyAssignmentDTO> response = companyAssignmentService.getCompanyAssignmentsByEmployee(employeeId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyAssignmentDTO> createCompanyAssignment(@Valid @RequestBody CompanyAssignmentCreateDTO createDTO) {
        CompanyAssignmentDTO assignment = companyAssignmentService.createCompanyAssignment(createDTO);
        return ResponseEntity.ok(assignment);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyAssignmentDTO> updateCompanyAssignment(
            @PathVariable Long id,
            @Valid @RequestBody CompanyAssignmentUpdateDTO updateDTO) {
        CompanyAssignmentDTO assignment = companyAssignmentService.updateCompanyAssignment(id, updateDTO);
        return ResponseEntity.ok(assignment);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCompanyAssignment(@PathVariable Long id) {
        companyAssignmentService.deleteCompanyAssignment(id);
        return ResponseEntity.noContent().build();
    }
}

