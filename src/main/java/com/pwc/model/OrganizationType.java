package com.pwc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "organization_types", indexes = {
    @Index(name = "idx_org_types_active", columnList = "active"),
    @Index(name = "idx_org_types_slug", columnList = "slug")
})
public class OrganizationType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;  // Must match OrganizationDetail.organizationType exactly
    
    @Column(nullable = false, unique = true, length = 100)
    private String slug;  // URL-friendly: "academic-unit"
    
    @Column(nullable = false, length = 200)
    private String displayName;  // "Academic Unit Assignments"
    
    @Column(length = 50)
    private String iconName;  // Lucide icon: "GraduationCap", "Gift", "Building2"
    
    @Column
    private Integer displayOrder = 0;
    
    @Column
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "organizationType", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("displayOrder ASC")
    private List<AssignmentFieldDefinition> fieldDefinitions = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public OrganizationType() {}
    
    public OrganizationType(String name, String slug, String displayName, String iconName, Integer displayOrder) {
        this.name = name;
        this.slug = slug;
        this.displayName = displayName;
        this.iconName = iconName;
        this.displayOrder = displayOrder;
        this.active = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<AssignmentFieldDefinition> getFieldDefinitions() { return fieldDefinitions; }
    public void setFieldDefinitions(List<AssignmentFieldDefinition> fieldDefinitions) { this.fieldDefinitions = fieldDefinitions; }
}
