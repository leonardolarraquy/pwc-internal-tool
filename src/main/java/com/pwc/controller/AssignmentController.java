package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.AssignmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {
    
    private final AssignmentService service;
    
    public AssignmentController(AssignmentService service) {
        this.service = service;
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<Long, Long>> getAssignmentStats() {
        return ResponseEntity.ok(service.getAssignmentCountsByOrgType());
    }
    
    @GetMapping
    public ResponseEntity<PageResponse<AssignmentDTO>> getAssignments(
            @RequestParam String orgTypeSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "") String search) {
        return ResponseEntity.ok(service.getAssignmentsByOrgType(orgTypeSlug, page, size, sortBy, sortDir, search));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AssignmentDTO> getAssignmentById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getAssignmentById(id));
    }
    
    @GetMapping("/by-org-detail/{orgDetailId}")
    public ResponseEntity<List<AssignmentDTO>> getAssignmentsByOrgDetail(@PathVariable Long orgDetailId) {
        return ResponseEntity.ok(service.getAssignmentsByOrgDetailId(orgDetailId));
    }
    
    @PostMapping
    public ResponseEntity<AssignmentDTO> createAssignment(@RequestBody AssignmentCreateDTO createDTO) {
        return ResponseEntity.ok(service.createAssignment(createDTO));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AssignmentDTO> updateAssignment(
            @PathVariable Long id,
            @RequestBody AssignmentUpdateDTO updateDTO) {
        return ResponseEntity.ok(service.updateAssignment(id, updateDTO));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable Long id) {
        service.deleteAssignment(id);
        return ResponseEntity.ok().build();
    }
}
