package com.pwc.dto;

import com.pwc.model.Role;
import java.util.HashMap;
import java.util.Map;

public class UserDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String company;
    private String email;
    private Role role;
    
    // Dynamic organization access: key = organizationTypeId, value = hasAccess
    private Map<Long, Boolean> organizationAccess = new HashMap<>();
    
    public UserDTO() {
    }
    
    public UserDTO(Long id, String firstName, String lastName, String company, String email, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.company = company;
        this.email = email;
        this.role = role;
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
    
    public Map<Long, Boolean> getOrganizationAccess() {
        return organizationAccess;
    }
    
    public void setOrganizationAccess(Map<Long, Boolean> organizationAccess) {
        this.organizationAccess = organizationAccess;
    }
}
