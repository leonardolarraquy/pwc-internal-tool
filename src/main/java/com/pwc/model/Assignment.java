package com.pwc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "assignments", indexes = {
    @Index(name = "idx_assignments_employee", columnList = "employee_id"),
    @Index(name = "idx_assignments_org_detail", columnList = "organization_detail_id"),
    @Index(name = "idx_assignments_user", columnList = "user_id"),
    @Index(name = "idx_assignments_created_at", columnList = "created_at")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_assignment_employee_org", columnNames = {"employee_id", "organization_detail_id"})
})
public class Assignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_detail_id", nullable = false)
    private OrganizationDetail organizationDetail;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User createdBy;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentFieldValue> fieldValues = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Constructors
    public Assignment() {}
    
    public Assignment(Employee employee, OrganizationDetail organizationDetail, User createdBy) {
        this.employee = employee;
        this.organizationDetail = organizationDetail;
        this.createdBy = createdBy;
    }
    
    // Helper method to add field value
    public void addFieldValue(AssignmentFieldValue fieldValue) {
        fieldValues.add(fieldValue);
        fieldValue.setAssignment(this);
    }
    
    // Helper method to remove field value
    public void removeFieldValue(AssignmentFieldValue fieldValue) {
        fieldValues.remove(fieldValue);
        fieldValue.setAssignment(null);
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    
    public OrganizationDetail getOrganizationDetail() { return organizationDetail; }
    public void setOrganizationDetail(OrganizationDetail organizationDetail) { this.organizationDetail = organizationDetail; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<AssignmentFieldValue> getFieldValues() { return fieldValues; }
    public void setFieldValues(List<AssignmentFieldValue> fieldValues) { this.fieldValues = fieldValues; }
}
