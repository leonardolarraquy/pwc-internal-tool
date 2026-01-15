package com.pwc.dto;

import java.util.Map;

public class AssignmentCreateDTO {
    private Long employeeId;
    private Long organizationDetailId;
    private Map<String, Boolean> fieldValues;  // fieldKey -> value
    
    public AssignmentCreateDTO() {}
    
    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public Long getOrganizationDetailId() { return organizationDetailId; }
    public void setOrganizationDetailId(Long organizationDetailId) { this.organizationDetailId = organizationDetailId; }
    
    public Map<String, Boolean> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, Boolean> fieldValues) { this.fieldValues = fieldValues; }
}
