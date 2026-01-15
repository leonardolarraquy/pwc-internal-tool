package com.pwc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_field_definitions", indexes = {
    @Index(name = "idx_field_defs_org_type", columnList = "organization_type_id"),
    @Index(name = "idx_field_defs_active", columnList = "organization_type_id, active")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organization_type_id", "field_key"})
})
public class AssignmentFieldDefinition {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_type_id", nullable = false)
    private OrganizationType organizationType;
    
    @Column(nullable = false, length = 100)
    private String fieldKey;  // Internal identifier: "finGiftFinancialAnalyst"
    
    @Column(nullable = false, length = 200)
    private String fieldTitle;  // Display title: "FIN Gift Financial Analyst"
    
    @Column(columnDefinition = "TEXT")
    private String fieldDescription;  // Long description for tooltips
    
    @Column
    private Integer displayOrder = 0;
    
    @Column
    private Boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public AssignmentFieldDefinition() {}
    
    public AssignmentFieldDefinition(OrganizationType organizationType, String fieldKey, String fieldTitle, String fieldDescription, Integer displayOrder) {
        this.organizationType = organizationType;
        this.fieldKey = fieldKey;
        this.fieldTitle = fieldTitle;
        this.fieldDescription = fieldDescription;
        this.displayOrder = displayOrder;
        this.active = true;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public OrganizationType getOrganizationType() { return organizationType; }
    public void setOrganizationType(OrganizationType organizationType) { this.organizationType = organizationType; }
    
    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
    
    public String getFieldTitle() { return fieldTitle; }
    public void setFieldTitle(String fieldTitle) { this.fieldTitle = fieldTitle; }
    
    public String getFieldDescription() { return fieldDescription; }
    public void setFieldDescription(String fieldDescription) { this.fieldDescription = fieldDescription; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
