package com.pwc.dto;

public class OrganizationDetailDTO {
    private Long id;
    private String legacyOrganizationName;
    private String organization;
    private String organizationType;
    private String referenceId;
    
    public OrganizationDetailDTO() {
    }
    
    public OrganizationDetailDTO(Long id, String legacyOrganizationName, String organization, String organizationType, String referenceId) {
        this.id = id;
        this.legacyOrganizationName = legacyOrganizationName;
        this.organization = organization;
        this.organizationType = organizationType;
        this.referenceId = referenceId;
    }
    
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






