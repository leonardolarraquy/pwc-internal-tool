package com.pwc.controller;

import com.pwc.model.AppParameter;
import com.pwc.service.AppParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/parameters")
public class AppParameterController {
    
    @Autowired
    private AppParameterService service;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppParameter>> getAllParameters() {
        return ResponseEntity.ok(service.getAllParameters());
    }
    
    @GetMapping("/{key}")
    public ResponseEntity<AppParameter> getParameter(@PathVariable String key) {
        return service.getParameterByKey(key)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/image/{key}")
    public ResponseEntity<Resource> getImage(@PathVariable String key) {
        try {
            AppParameter param = service.getParameterByKey(key)
                .orElseThrow(() -> new RuntimeException("Parameter not found"));
            
            if (!"image".equals(param.getParamType())) {
                return ResponseEntity.badRequest().build();
            }
            
            Path filePath = Paths.get(service.getUploadDir()).resolve(param.getParamValue());
            Resource resource = new UrlResource(filePath.toUri());
            
            if (resource.exists() && resource.isReadable()) {
                // Detect content type
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "image/png";
                }
                
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppParameter> saveParameter(@RequestBody AppParameter parameter) {
        return ResponseEntity.ok(service.saveParameter(parameter));
    }
    
    @PostMapping("/upload/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AppParameter> uploadImage(
            @PathVariable String key,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        try {
            AppParameter param = service.saveImageParameter(key, file, description);
            return ResponseEntity.ok(param);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @DeleteMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteParameter(@PathVariable String key) {
        service.deleteParameter(key);
        return ResponseEntity.ok().build();
    }
}

