package com.pwc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "employee_id", nullable = false)
    private String employeeId;
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "position_id")
    private String positionId;
    
    @Column(name = "position_title")
    private String positionTitle;
    
    @Column(name = "email", nullable = false)
    private String email;
    
    @Column(name = "password", nullable = true)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role = Role.USER;
    
    @Column(name = "company_assignments_access", nullable = true)
    private Boolean companyAssignmentsAccess;
    
    @Column(name = "academic_unit_assignments_access", nullable = true)
    private Boolean academicUnitAssignmentsAccess;
    
    @Column(name = "gift_assignments_access", nullable = true)
    private Boolean giftAssignmentsAccess;
    
    public User() {
    }
    
    public User(Long id, String employeeId, String firstName, String lastName, String positionId, String positionTitle, String email, String password, Role role) {
        this.id = id;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.positionId = positionId;
        this.positionTitle = positionTitle;
        this.email = email;
        this.password = password;
        this.role = role;
        this.companyAssignmentsAccess = null;
        this.academicUnitAssignmentsAccess = null;
        this.giftAssignmentsAccess = null;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPositionId() {
        return positionId;
    }
    
    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }
    
    public String getPositionTitle() {
        return positionTitle;
    }
    
    public void setPositionTitle(String positionTitle) {
        this.positionTitle = positionTitle;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Boolean getCompanyAssignmentsAccess() {
        return companyAssignmentsAccess != null ? companyAssignmentsAccess : false;
    }
    
    public void setCompanyAssignmentsAccess(Boolean companyAssignmentsAccess) {
        this.companyAssignmentsAccess = companyAssignmentsAccess;
    }
    
    public Boolean getAcademicUnitAssignmentsAccess() {
        return academicUnitAssignmentsAccess != null ? academicUnitAssignmentsAccess : false;
    }
    
    public void setAcademicUnitAssignmentsAccess(Boolean academicUnitAssignmentsAccess) {
        this.academicUnitAssignmentsAccess = academicUnitAssignmentsAccess;
    }
    
    public Boolean getGiftAssignmentsAccess() {
        return giftAssignmentsAccess != null ? giftAssignmentsAccess : false;
    }
    
    public void setGiftAssignmentsAccess(Boolean giftAssignmentsAccess) {
        this.giftAssignmentsAccess = giftAssignmentsAccess;
    }
}

