package com.pwc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_parameters")
public class AppParameter {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String paramKey;
    
    @Column(columnDefinition = "TEXT")
    private String paramValue;
    
    @Column
    private String paramType; // "text", "image", "boolean", etc.
    
    @Column
    private String description;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getParamKey() { return paramKey; }
    public void setParamKey(String paramKey) { this.paramKey = paramKey; }
    
    public String getParamValue() { return paramValue; }
    public void setParamValue(String paramValue) { this.paramValue = paramValue; }
    
    public String getParamType() { return paramType; }
    public void setParamType(String paramType) { this.paramType = paramType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

