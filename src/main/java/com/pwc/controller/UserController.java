package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.model.User;
import com.pwc.repository.AcademicUnitAssignmentRepository;
import com.pwc.repository.CompanyAssignmentRepository;
import com.pwc.repository.GiftAssignmentRepository;
import com.pwc.service.UserService;
import com.pwc.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    private final CompanyAssignmentRepository companyAssignmentRepository;
    private final AcademicUnitAssignmentRepository academicUnitAssignmentRepository;
    private final GiftAssignmentRepository giftAssignmentRepository;
    private final SecurityUtil securityUtil;
    
    public UserController(
            UserService userService,
            CompanyAssignmentRepository companyAssignmentRepository,
            AcademicUnitAssignmentRepository academicUnitAssignmentRepository,
            GiftAssignmentRepository giftAssignmentRepository,
            SecurityUtil securityUtil) {
        this.userService = userService;
        this.companyAssignmentRepository = companyAssignmentRepository;
        this.academicUnitAssignmentRepository = academicUnitAssignmentRepository;
        this.giftAssignmentRepository = giftAssignmentRepository;
        this.securityUtil = securityUtil;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String accessFilter) {
        
        PageResponse<UserDTO> response = userService.getAllUsers(page, size, sortBy, sortDir, search, accessFilter);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getUserStatistics() {
        Map<String, Long> stats = userService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/my-assignment-stats")
    public ResponseEntity<Map<String, Long>> getMyAssignmentStats() {
        User currentUser = securityUtil.getCurrentUser();
        
        Map<String, Long> stats = new HashMap<>();
        stats.put("companyAssignments", companyAssignmentRepository.countByCreatedById(currentUser.getId()));
        stats.put("academicUnitAssignments", academicUnitAssignmentRepository.countByCreatedById(currentUser.getId()));
        stats.put("giftAssignments", giftAssignmentRepository.countByCreatedById(currentUser.getId()));
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO user = userService.createUser(userCreateDTO);
        return ResponseEntity.ok(user);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, 
                                              @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO user = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(user);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password reset successfully. User must set a new password on next login.");
        return ResponseEntity.ok(response);
    }
}
