package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.AcademicUnitAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/academic-unit-assignments")
public class AcademicUnitAssignmentController {
    
    private final AcademicUnitAssignmentService academicUnitAssignmentService;
    
    public AcademicUnitAssignmentController(AcademicUnitAssignmentService academicUnitAssignmentService) {
        this.academicUnitAssignmentService = academicUnitAssignmentService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AcademicUnitAssignmentDTO>> getAllAcademicUnitAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        PageResponse<AcademicUnitAssignmentDTO> response = academicUnitAssignmentService.getAllAcademicUnitAssignments(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicUnitAssignmentDTO> getAcademicUnitAssignmentById(@PathVariable Long id) {
        AcademicUnitAssignmentDTO assignment = academicUnitAssignmentService.getAcademicUnitAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }
    
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<AcademicUnitAssignmentDTO>> getAcademicUnitAssignmentsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        PageResponse<AcademicUnitAssignmentDTO> response = academicUnitAssignmentService.getAcademicUnitAssignmentsByUser(userId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicUnitAssignmentDTO> createAcademicUnitAssignment(@Valid @RequestBody AcademicUnitAssignmentCreateDTO createDTO) {
        AcademicUnitAssignmentDTO assignment = academicUnitAssignmentService.createAcademicUnitAssignment(createDTO);
        return ResponseEntity.ok(assignment);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AcademicUnitAssignmentDTO> updateAcademicUnitAssignment(
            @PathVariable Long id,
            @Valid @RequestBody AcademicUnitAssignmentUpdateDTO updateDTO) {
        AcademicUnitAssignmentDTO assignment = academicUnitAssignmentService.updateAcademicUnitAssignment(id, updateDTO);
        return ResponseEntity.ok(assignment);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAcademicUnitAssignment(@PathVariable Long id) {
        academicUnitAssignmentService.deleteAcademicUnitAssignment(id);
        return ResponseEntity.noContent().build();
    }
}

