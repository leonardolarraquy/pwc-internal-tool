package com.pwc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gift_assignments")
public class GiftAssignment extends BaseAssignment {
    
    @Column(name = "fin_gift_financial_analyst")
    private Boolean finGiftFinancialAnalyst = false;
    
    @Column(name = "fin_gift_manager")
    private Boolean finGiftManager = false;
    
    @Column(name = "fin_professorship_partner_gift")
    private Boolean finProfessorshipPartnerGift = false;
    
    public GiftAssignment() {
        super();
    }
    
    public GiftAssignment(Employee employee, OrganizationDetail organizationDetail, User createdBy) {
        super();
        setEmployee(employee);
        setOrganizationDetail(organizationDetail);
        setCreatedBy(createdBy);
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






