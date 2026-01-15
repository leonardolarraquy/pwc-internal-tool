package com.pwc.dto;

import com.pwc.model.Role;
import jakarta.validation.constraints.Email;
import java.util.HashMap;
import java.util.Map;

public class UserUpdateDTO {
    private String firstName;
    private String lastName;
    private String company;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String password;
    private Role role;
    
    // Dynamic organization access: key = organizationTypeId, value = hasAccess
    private Map<Long, Boolean> organizationAccess = new HashMap<>();
    
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
    
    public Map<Long, Boolean> getOrganizationAccess() {
        return organizationAccess;
    }
    
    public void setOrganizationAccess(Map<Long, Boolean> organizationAccess) {
        this.organizationAccess = organizationAccess;
    }
}
