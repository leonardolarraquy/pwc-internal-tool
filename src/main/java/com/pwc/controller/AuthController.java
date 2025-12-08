package com.pwc.controller;

import com.pwc.dto.ChangePasswordRequest;
import com.pwc.dto.LoginRequest;
import com.pwc.dto.LoginResponse;
import com.pwc.service.UserService;
import com.pwc.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.authenticate(request.getEmail(), request.getPassword());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(response.getEmail(), response.getRole().name());
        response.setToken(token);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request.getEmail(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}

