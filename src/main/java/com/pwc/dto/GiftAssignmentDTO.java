package com.pwc.dto;

public class GiftAssignmentDTO {
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



