package com.pwc.dto;

public class GiftAssignmentDTO {
    private Long id;
    private Long employeeId;
    private String employeeFirstName;
    private String employeeLastName;
    private String employeeEmail;
    private String employeeEmployeeId;
    private Long organizationDetailId;
    private String organizationName;
    private String organizationType;
    private String referenceId;
    private Boolean finGiftFinancialAnalyst;
    private Boolean finGiftManager;
    private Boolean finProfessorshipPartnerGift;
    
    public GiftAssignmentDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getEmployeeFirstName() {
        return employeeFirstName;
    }
    
    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }
    
    public String getEmployeeLastName() {
        return employeeLastName;
    }
    
    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }
    
    public String getEmployeeEmail() {
        return employeeEmail;
    }
    
    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
    
    public String getEmployeeEmployeeId() {
        return employeeEmployeeId;
    }
    
    public void setEmployeeEmployeeId(String employeeEmployeeId) {
        this.employeeEmployeeId = employeeEmployeeId;
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
    
    public Boolean getFinGiftFinancialAnalyst() {
        return finGiftFinancialAnalyst != null ? finGiftFinancialAnalyst : false;
    }
    
    public void setFinGiftFinancialAnalyst(Boolean finGiftFinancialAnalyst) {
        this.finGiftFinancialAnalyst = finGiftFinancialAnalyst;
    }
    
    public Boolean getFinGiftManager() {
        return finGiftManager != null ? finGiftManager : false;
    }
    
    public void setFinGiftManager(Boolean finGiftManager) {
        this.finGiftManager = finGiftManager;
    }
    
    public Boolean getFinProfessorshipPartnerGift() {
        return finProfessorshipPartnerGift != null ? finProfessorshipPartnerGift : false;
    }
    
    public void setFinProfessorshipPartnerGift(Boolean finProfessorshipPartnerGift) {
        this.finProfessorshipPartnerGift = finProfessorshipPartnerGift;
    }
}






