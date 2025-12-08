package com.pwc.dto;

import com.pwc.model.Role;

public class UserDTO {
    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String positionId;
    private String positionTitle;
    private String email;
    private Role role;
    private Boolean companyAssignmentsAccess;
    private Boolean academicUnitAssignmentsAccess;
    private Boolean giftAssignmentsAccess;
    
    public UserDTO() {
    }
    
    public UserDTO(Long id, String employeeId, String firstName, String lastName, String positionId, String positionTitle, String email, Role role, Boolean companyAssignmentsAccess, Boolean academicUnitAssignmentsAccess, Boolean giftAssignmentsAccess) {
        this.id = id;
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.positionId = positionId;
        this.positionTitle = positionTitle;
        this.email = email;
        this.role = role;
        this.companyAssignmentsAccess = companyAssignmentsAccess;
        this.academicUnitAssignmentsAccess = academicUnitAssignmentsAccess;
        this.giftAssignmentsAccess = giftAssignmentsAccess;
    }
    
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


