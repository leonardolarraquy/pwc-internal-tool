package com.pwc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_organization_access",
       indexes = {
           @Index(name = "idx_user_org_access_user", columnList = "user_id"),
           @Index(name = "idx_user_org_access_org_type", columnList = "organization_type_id")
       },
       uniqueConstraints = @UniqueConstraint(
           name = "uk_user_org_access", 
           columnNames = {"user_id", "organization_type_id"}
       ))
public class UserOrganizationAccess {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_type_id", nullable = false)
    private OrganizationType organizationType;
    
    @Column(name = "has_access", nullable = false)
    private Boolean hasAccess = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public UserOrganizationAccess() {}
    
    public UserOrganizationAccess(User user, OrganizationType organizationType, Boolean hasAccess) {
        this.user = user;
        this.organizationType = organizationType;
        this.hasAccess = hasAccess;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public OrganizationType getOrganizationType() { return organizationType; }
    public void setOrganizationType(OrganizationType organizationType) { this.organizationType = organizationType; }
    
    public Boolean getHasAccess() { return hasAccess; }
    public void setHasAccess(Boolean hasAccess) { this.hasAccess = hasAccess; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
