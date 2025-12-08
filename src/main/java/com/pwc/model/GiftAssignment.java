package com.pwc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gift_assignments")
public class GiftAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_detail_id", nullable = false)
    private OrganizationDetail organizationDetail;
    
    @Column(name = "fin_gift_financial_analyst")
    private Boolean finGiftFinancialAnalyst = false;
    
    @Column(name = "fin_gift_manager")
    private Boolean finGiftManager = false;
    
    @Column(name = "fin_professorship_partner_gift")
    private Boolean finProfessorshipPartnerGift = false;
    
    public GiftAssignment() {
    }
    
    public GiftAssignment(User user, OrganizationDetail organizationDetail) {
        this.user = user;
        this.organizationDetail = organizationDetail;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public OrganizationDetail getOrganizationDetail() {
        return organizationDetail;
    }
    
    public void setOrganizationDetail(OrganizationDetail organizationDetail) {
        this.organizationDetail = organizationDetail;
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



