package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.GiftAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gift-assignments")
public class GiftAssignmentController {
    
    private final GiftAssignmentService giftAssignmentService;
    
    public GiftAssignmentController(GiftAssignmentService giftAssignmentService) {
        this.giftAssignmentService = giftAssignmentService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<GiftAssignmentDTO>> getAllGiftAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        PageResponse<GiftAssignmentDTO> response = giftAssignmentService.getAllGiftAssignments(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftAssignmentDTO> getGiftAssignmentById(@PathVariable Long id) {
        GiftAssignmentDTO assignment = giftAssignmentService.getGiftAssignmentById(id);
        return ResponseEntity.ok(assignment);
    }
    
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<GiftAssignmentDTO>> getGiftAssignmentsByEmployee(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        
        PageResponse<GiftAssignmentDTO> response = giftAssignmentService.getGiftAssignmentsByEmployee(employeeId, page, size);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftAssignmentDTO> createGiftAssignment(@Valid @RequestBody GiftAssignmentCreateDTO createDTO) {
        GiftAssignmentDTO assignment = giftAssignmentService.createGiftAssignment(createDTO);
        return ResponseEntity.ok(assignment);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiftAssignmentDTO> updateGiftAssignment(
            @PathVariable Long id,
            @Valid @RequestBody GiftAssignmentUpdateDTO updateDTO) {
        GiftAssignmentDTO assignment = giftAssignmentService.updateGiftAssignment(id, updateDTO);
        return ResponseEntity.ok(assignment);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGiftAssignment(@PathVariable Long id) {
        giftAssignmentService.deleteGiftAssignment(id);
        return ResponseEntity.noContent().build();
    }
}

