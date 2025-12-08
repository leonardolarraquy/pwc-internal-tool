package com.pwc.dto;

import jakarta.validation.constraints.NotNull;

public class GiftAssignmentCreateDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Organization Detail ID is required")
    private Long organizationDetailId;
    
    private Boolean finGiftFinancialAnalyst;
    private Boolean finGiftManager;
    private Boolean finProfessorshipPartnerGift;
    
    public GiftAssignmentCreateDTO() {
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



