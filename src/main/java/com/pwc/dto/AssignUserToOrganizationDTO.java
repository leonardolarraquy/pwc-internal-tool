package com.pwc.dto;

import jakarta.validation.constraints.NotNull;

public class AssignUserToOrganizationDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    // Gift assignment fields
    private Boolean finGiftFinancialAnalyst;
    private Boolean finGiftManager;
    private Boolean finProfessorshipPartnerGift;
    
    // Academic Unit assignment fields
    private Boolean hcmAcademicChairAu;
    private Boolean hcmAcademicDeanAuh;
    private Boolean hcmAcademicFacultyExecutiveAuh;
    private Boolean hcmAcademicFacultyHrAnalystAu;
    private Boolean hcmAcademicProvostPartnerAuh;
    private Boolean hcmAcademicSchoolDirectorAuh;
    
    public AssignUserToOrganizationDTO() {
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
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
    
    public Boolean getHcmAcademicChairAu() {
        return hcmAcademicChairAu != null ? hcmAcademicChairAu : false;
    }
    
    public void setHcmAcademicChairAu(Boolean hcmAcademicChairAu) {
        this.hcmAcademicChairAu = hcmAcademicChairAu;
    }
    
    public Boolean getHcmAcademicDeanAuh() {
        return hcmAcademicDeanAuh != null ? hcmAcademicDeanAuh : false;
    }
    
    public void setHcmAcademicDeanAuh(Boolean hcmAcademicDeanAuh) {
        this.hcmAcademicDeanAuh = hcmAcademicDeanAuh;
    }
    
    public Boolean getHcmAcademicFacultyExecutiveAuh() {
        return hcmAcademicFacultyExecutiveAuh != null ? hcmAcademicFacultyExecutiveAuh : false;
    }
    
    public void setHcmAcademicFacultyExecutiveAuh(Boolean hcmAcademicFacultyExecutiveAuh) {
        this.hcmAcademicFacultyExecutiveAuh = hcmAcademicFacultyExecutiveAuh;
    }
    
    public Boolean getHcmAcademicFacultyHrAnalystAu() {
        return hcmAcademicFacultyHrAnalystAu != null ? hcmAcademicFacultyHrAnalystAu : false;
    }
    
    public void setHcmAcademicFacultyHrAnalystAu(Boolean hcmAcademicFacultyHrAnalystAu) {
        this.hcmAcademicFacultyHrAnalystAu = hcmAcademicFacultyHrAnalystAu;
    }
    
    public Boolean getHcmAcademicProvostPartnerAuh() {
        return hcmAcademicProvostPartnerAuh != null ? hcmAcademicProvostPartnerAuh : false;
    }
    
    public void setHcmAcademicProvostPartnerAuh(Boolean hcmAcademicProvostPartnerAuh) {
        this.hcmAcademicProvostPartnerAuh = hcmAcademicProvostPartnerAuh;
    }
    
    public Boolean getHcmAcademicSchoolDirectorAuh() {
        return hcmAcademicSchoolDirectorAuh != null ? hcmAcademicSchoolDirectorAuh : false;
    }
    
    public void setHcmAcademicSchoolDirectorAuh(Boolean hcmAcademicSchoolDirectorAuh) {
        this.hcmAcademicSchoolDirectorAuh = hcmAcademicSchoolDirectorAuh;
    }
}










