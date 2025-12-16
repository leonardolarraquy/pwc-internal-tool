package com.pwc.dto;

import jakarta.validation.constraints.Size;

public class OrganizationDetailUpdateDTO {
    
    @Size(max = 100, message = "Legacy Organization Name must not exceed 100 characters")
    private String legacyOrganizationName;
    
    @Size(max = 100, message = "Organization must not exceed 100 characters")
    private String organization;
    
    @Size(max = 100, message = "Organization Type must not exceed 100 characters")
    private String organizationType;
    
    @Size(max = 100, message = "Reference ID must not exceed 100 characters")
    private String referenceId;
    
    public OrganizationDetailUpdateDTO() {
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












