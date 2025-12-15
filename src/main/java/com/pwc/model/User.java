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
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;
    
    @Column(name = "company")
    private String company;
    
    @Column(name = "email", nullable = false, unique = true)
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
    
    @Column(name = "location_assignments_access", nullable = true)
    private Boolean locationAssignmentsAccess;
    
    @Column(name = "project_assignments_access", nullable = true)
    private Boolean projectAssignmentsAccess;
    
    @Column(name = "grant_assignments_access", nullable = true)
    private Boolean grantAssignmentsAccess;
    
    @Column(name = "paygroup_assignments_access", nullable = true)
    private Boolean paygroupAssignmentsAccess;
    
    public User() {
    }
    
    public User(Long id, String firstName, String lastName, String company, String email, String password, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.email = email;
        this.password = password;
        this.role = role;
        this.companyAssignmentsAccess = null;
        this.academicUnitAssignmentsAccess = null;
        this.giftAssignmentsAccess = null;
        this.locationAssignmentsAccess = null;
        this.projectAssignmentsAccess = null;
        this.grantAssignmentsAccess = null;
        this.paygroupAssignmentsAccess = null;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getCompany() {
        return company;
    }
    
    public void setCompany(String company) {
        this.company = company;
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
    
    public Boolean getLocationAssignmentsAccess() {
        return locationAssignmentsAccess != null ? locationAssignmentsAccess : false;
    }
    
    public void setLocationAssignmentsAccess(Boolean locationAssignmentsAccess) {
        this.locationAssignmentsAccess = locationAssignmentsAccess;
    }
    
    public Boolean getProjectAssignmentsAccess() {
        return projectAssignmentsAccess != null ? projectAssignmentsAccess : false;
    }
    
    public void setProjectAssignmentsAccess(Boolean projectAssignmentsAccess) {
        this.projectAssignmentsAccess = projectAssignmentsAccess;
    }
    
    public Boolean getGrantAssignmentsAccess() {
        return grantAssignmentsAccess != null ? grantAssignmentsAccess : false;
    }
    
    public void setGrantAssignmentsAccess(Boolean grantAssignmentsAccess) {
        this.grantAssignmentsAccess = grantAssignmentsAccess;
    }
    
    public Boolean getPaygroupAssignmentsAccess() {
        return paygroupAssignmentsAccess != null ? paygroupAssignmentsAccess : false;
    }
    
    public void setPaygroupAssignmentsAccess(Boolean paygroupAssignmentsAccess) {
        this.paygroupAssignmentsAccess = paygroupAssignmentsAccess;
    }
}
