package com.pwc.dto;

import com.pwc.model.Role;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String company;
    private String email;
    private Role role;
    private Boolean companyAssignmentsAccess;
    private Boolean academicUnitAssignmentsAccess;
    private Boolean giftAssignmentsAccess;
    private Boolean locationAssignmentsAccess;
    private Boolean projectAssignmentsAccess;
    private Boolean grantAssignmentsAccess;
    private Boolean paygroupAssignmentsAccess;
    
    public UserDTO() {
    }
    
    public UserDTO(Long id, String firstName, String lastName, String company, String email, Role role, Boolean companyAssignmentsAccess, Boolean academicUnitAssignmentsAccess, Boolean giftAssignmentsAccess, Boolean locationAssignmentsAccess, Boolean projectAssignmentsAccess, Boolean grantAssignmentsAccess, Boolean paygroupAssignmentsAccess) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.email = email;
        this.role = role;
        this.companyAssignmentsAccess = companyAssignmentsAccess;
        this.academicUnitAssignmentsAccess = academicUnitAssignmentsAccess;
        this.giftAssignmentsAccess = giftAssignmentsAccess;
        this.locationAssignmentsAccess = locationAssignmentsAccess;
        this.projectAssignmentsAccess = projectAssignmentsAccess;
        this.grantAssignmentsAccess = grantAssignmentsAccess;
        this.paygroupAssignmentsAccess = paygroupAssignmentsAccess;
    }
    
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
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Boolean getCompanyAssignmentsAccess() {
        return companyAssignmentsAccess;
    }
    
    public void setCompanyAssignmentsAccess(Boolean companyAssignmentsAccess) {
        this.companyAssignmentsAccess = companyAssignmentsAccess;
    }
    
    public Boolean getAcademicUnitAssignmentsAccess() {
        return academicUnitAssignmentsAccess;
    }
    
    public void setAcademicUnitAssignmentsAccess(Boolean academicUnitAssignmentsAccess) {
        this.academicUnitAssignmentsAccess = academicUnitAssignmentsAccess;
    }
    
    public Boolean getGiftAssignmentsAccess() {
        return giftAssignmentsAccess;
    }
    
    public void setGiftAssignmentsAccess(Boolean giftAssignmentsAccess) {
        this.giftAssignmentsAccess = giftAssignmentsAccess;
    }
    
    public Boolean getLocationAssignmentsAccess() {
        return locationAssignmentsAccess;
    }
    
    public void setLocationAssignmentsAccess(Boolean locationAssignmentsAccess) {
        this.locationAssignmentsAccess = locationAssignmentsAccess;
    }
    
    public Boolean getProjectAssignmentsAccess() {
        return projectAssignmentsAccess;
    }
    
    public void setProjectAssignmentsAccess(Boolean projectAssignmentsAccess) {
        this.projectAssignmentsAccess = projectAssignmentsAccess;
    }
    
    public Boolean getGrantAssignmentsAccess() {
        return grantAssignmentsAccess;
    }
    
    public void setGrantAssignmentsAccess(Boolean grantAssignmentsAccess) {
        this.grantAssignmentsAccess = grantAssignmentsAccess;
    }
    
    public Boolean getPaygroupAssignmentsAccess() {
        return paygroupAssignmentsAccess;
    }
    
    public void setPaygroupAssignmentsAccess(Boolean paygroupAssignmentsAccess) {
        this.paygroupAssignmentsAccess = paygroupAssignmentsAccess;
    }
}
