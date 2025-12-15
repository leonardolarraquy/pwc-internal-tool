package com.pwc.dto;

import jakarta.validation.constraints.NotNull;

public class CompanyAssignmentCreateDTO {
    
    @NotNull(message = "Employee ID is required")
    private Long employeeId;
    
    @NotNull(message = "Organization Detail ID is required")
    private Long organizationDetailId;
    
    public CompanyAssignmentCreateDTO() {
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public Long getOrganizationDetailId() {
        return organizationDetailId;
    }
    
    public void setOrganizationDetailId(Long organizationDetailId) {
        this.organizationDetailId = organizationDetailId;
    }
}






