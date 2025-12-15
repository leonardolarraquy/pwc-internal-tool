package com.pwc.dto;

import com.pwc.model.Role;
import jakarta.validation.constraints.Email;

public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String company;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String password;
    private Role role;
    private Boolean companyAssignmentsAccess;
    private Boolean academicUnitAssignmentsAccess;
    private Boolean giftAssignmentsAccess;
    private Boolean locationAssignmentsAccess;
    private Boolean projectAssignmentsAccess;
    private Boolean grantAssignmentsAccess;
    private Boolean paygroupAssignmentsAccess;
    
    public UserUpdateDTO() {
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
