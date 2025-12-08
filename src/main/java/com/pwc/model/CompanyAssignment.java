package com.pwc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "company_assignments")
public class CompanyAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_detail_id", nullable = false)
    private OrganizationDetail organizationDetail;
    
    public CompanyAssignment() {
    }
    
    public CompanyAssignment(User user, OrganizationDetail organizationDetail) {
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
}



