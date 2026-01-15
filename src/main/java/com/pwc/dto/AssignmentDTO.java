package com.pwc.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class AssignmentDTO {
    private Long id;
    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeEmail;
    private String employeeWorkerId;
    private String employeePositionId;
    private Long organizationDetailId;
    private String organizationName;
    private String organizationType;
    private String referenceId;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private Map<String, Boolean> fieldValues;  // fieldKey -> value
    
    public AssignmentDTO() {}
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    
    public String getEmployeeFirstName() { return employeeFirstName; }
    public void setEmployeeFirstName(String employeeFirstName) { this.employeeFirstName = employeeFirstName; }
    
    public String getEmployeeLastName() { return employeeLastName; }
    public void setEmployeeLastName(String employeeLastName) { this.employeeLastName = employeeLastName; }
    
    public String getEmployeeEmail() { return employeeEmail; }
    public void setEmployeeEmail(String employeeEmail) { this.employeeEmail = employeeEmail; }
    
    public String getEmployeeWorkerId() { return employeeWorkerId; }
    public void setEmployeeWorkerId(String employeeWorkerId) { this.employeeWorkerId = employeeWorkerId; }
    
    public String getEmployeePositionId() { return employeePositionId; }
    public void setEmployeePositionId(String employeePositionId) { this.employeePositionId = employeePositionId; }
    
    public Long getOrganizationDetailId() { return organizationDetailId; }
    public void setOrganizationDetailId(Long organizationDetailId) { this.organizationDetailId = organizationDetailId; }
    
    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }
    
    public String getOrganizationType() { return organizationType; }
    public void setOrganizationType(String organizationType) { this.organizationType = organizationType; }
    
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    
    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }
    
    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public Map<String, Boolean> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, Boolean> fieldValues) { this.fieldValues = fieldValues; }
}
