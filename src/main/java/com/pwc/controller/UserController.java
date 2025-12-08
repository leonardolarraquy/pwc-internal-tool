package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
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
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importUsers(@RequestParam("file") MultipartFile file) {
        int importedCount = userService.importUsersFromCsv(file);
        Map<String, Object> response = new HashMap<>();
        response.put("imported", importedCount);
        response.put("message", "Successfully imported " + importedCount + " users");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id) {
        userService.resetPassword(id);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Password reset successfully. User must set a new password on next login.");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> searchUser(@RequestParam String query) {
        List<UserDTO> users = userService.searchUser(query);
        return ResponseEntity.ok(users);
    }
}

