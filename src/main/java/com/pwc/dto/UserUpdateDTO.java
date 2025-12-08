package com.pwc.dto;

import com.pwc.model.Role;
import jakarta.validation.constraints.Email;

public class UserUpdateDTO {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String positionId;
    private String positionTitle;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String password;
    private Role role;
    private Boolean companyAssignmentsAccess;
    private Boolean academicUnitAssignmentsAccess;
    private Boolean giftAssignmentsAccess;
    
    public UserUpdateDTO() {
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
}


