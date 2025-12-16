package com.pwc.service;

import com.pwc.model.AppParameter;
import com.pwc.repository.AppParameterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppParameterService {
    
    @Autowired
    private AppParameterRepository repository;
    
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;
    
    public List<AppParameter> getAllParameters() {
        return repository.findAll();
    }
    
    public Optional<AppParameter> getParameterByKey(String key) {
        return repository.findByParamKey(key);
    }
    
    public AppParameter saveParameter(AppParameter parameter) {
        // Check if parameter with this key already exists
        Optional<AppParameter> existing = repository.findByParamKey(parameter.getParamKey());
        if (existing.isPresent()) {
            AppParameter existingParam = existing.get();
            existingParam.setParamValue(parameter.getParamValue());
            existingParam.setParamType(parameter.getParamType());
            existingParam.setDescription(parameter.getDescription());
            return repository.save(existingParam);
        }
        return repository.save(parameter);
    }
    
    public AppParameter saveImageParameter(String key, MultipartFile file, String description) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".png";
        String newFilename = key + "_" + UUID.randomUUID().toString() + extension;
        
        // Save file
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Save or update parameter
        AppParameter param = repository.findByParamKey(key)
            .orElse(new AppParameter());
        
        // Delete old file if exists
        if (param.getParamValue() != null && !param.getParamValue().isEmpty()) {
            try {
                Files.deleteIfExists(Paths.get(uploadDir, param.getParamValue()));
            } catch (IOException ignored) {}
        }
        
        param.setParamKey(key);
        param.setParamValue(newFilename);
        param.setParamType("image");
        param.setDescription(description);
        
        return repository.save(param);
    }
    
    public void deleteParameter(String key) {
        repository.findByParamKey(key).ifPresent(param -> {
            // Delete file if it's an image
            if ("image".equals(param.getParamType()) && param.getParamValue() != null) {
                try {
                    Files.deleteIfExists(Paths.get(uploadDir, param.getParamValue()));
                } catch (IOException ignored) {}
            }
            repository.delete(param);
        });
    }
    
    public String getUploadDir() {
        return uploadDir;
    }
}

