package com.pwc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "organization_details")
public class OrganizationDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "legacy_organization_name", length = 100)
    private String legacyOrganizationName;
    
    @Column(name = "organization", length = 100)
    private String organization;
    
    @Column(name = "organization_type", length = 100)
    private String organizationType;
    
    @Column(name = "reference_id", length = 100)
    private String referenceId;
    
    public OrganizationDetail() {
    }
    
    public OrganizationDetail(String legacyOrganizationName, String organization, String organizationType, String referenceId) {
        this.legacyOrganizationName = legacyOrganizationName;
        this.organization = organization;
        this.organizationType = organizationType;
        this.referenceId = referenceId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLegacyOrganizationName() {
        return legacyOrganizationName;
    }
    
    public void setLegacyOrganizationName(String legacyOrganizationName) {
        this.legacyOrganizationName = legacyOrganizationName;
    }
    
    public String getOrganization() {
        return organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }
    
    public String getOrganizationType() {
        return organizationType;
    }
    
    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}









