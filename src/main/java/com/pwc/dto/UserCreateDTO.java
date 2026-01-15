package com.pwc.dto;

import com.pwc.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.Map;

public class UserCreateDTO {
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    private String company;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    // Password is optional - if null, user must set it on first login
    private String password;
    
    private Role role = Role.USER;
    
    // Dynamic organization access: key = organizationTypeId, value = hasAccess
    private Map<Long, Boolean> organizationAccess = new HashMap<>();
    
    public UserCreateDTO() {
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
