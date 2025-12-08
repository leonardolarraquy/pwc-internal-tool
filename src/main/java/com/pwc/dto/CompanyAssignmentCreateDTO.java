package com.pwc.dto;

import jakarta.validation.constraints.NotNull;

public class CompanyAssignmentCreateDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Organization Detail ID is required")
    private Long organizationDetailId;
    
    public CompanyAssignmentCreateDTO() {
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getOrganizationDetailId() {
        return organizationDetailId;
    }
    
    public void setOrganizationDetailId(Long organizationDetailId) {
        this.organizationDetailId = organizationDetailId;
    }
}



