package com.pwc.dto;

import com.pwc.model.Role;

public class LoginResponse {
    private String token;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private boolean mustChangePassword = false;
    private Boolean companyAssignmentsAccess = false;
    private Boolean academicUnitAssignmentsAccess = false;
    private Boolean giftAssignmentsAccess = false;
    private Boolean locationAssignmentsAccess = false;
    private Boolean projectAssignmentsAccess = false;
    private Boolean grantAssignmentsAccess = false;
    private Boolean paygroupAssignmentsAccess = false;
    
    public LoginResponse() {
    }
    
    public LoginResponse(String token, String email, Role role, String firstName, String lastName, boolean mustChangePassword) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mustChangePassword = mustChangePassword;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
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
    
    public boolean isMustChangePassword() {
        return mustChangePassword;
    }
    
    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
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

