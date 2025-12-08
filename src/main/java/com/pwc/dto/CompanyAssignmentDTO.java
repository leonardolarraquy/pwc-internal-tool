package com.pwc.dto;

public class CompanyAssignmentDTO {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userEmployeeId;
    private Long organizationDetailId;
    private String organizationName;
    private String organizationType;
    private String referenceId;
    
    public CompanyAssignmentDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserFirstName() {
        return userFirstName;
    }
    
    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }
    
    public String getUserLastName() {
        return userLastName;
    }
    
    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserEmployeeId() {
        return userEmployeeId;
    }
    
    public void setUserEmployeeId(String userEmployeeId) {
        this.userEmployeeId = userEmployeeId;
    }
    
    public Long getOrganizationDetailId() {
        return organizationDetailId;
    }
    
    public void setOrganizationDetailId(Long organizationDetailId) {
        this.organizationDetailId = organizationDetailId;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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



