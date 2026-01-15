package com.pwc.dto;

import com.pwc.model.Role;
import java.util.HashMap;
import java.util.Map;

public class LoginResponse {
    private String token;
    private String email;
    private Role role;
    private String firstName;
    private String lastName;
    private boolean mustChangePassword = false;
    
    // Dynamic organization access: key = organizationTypeId, value = hasAccess
    private Map<Long, Boolean> organizationAccess = new HashMap<>();
    
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
    
    public Map<Long, Boolean> getOrganizationAccess() {
        return organizationAccess;
    }
    
    public void setOrganizationAccess(Map<Long, Boolean> organizationAccess) {
        this.organizationAccess = organizationAccess;
    }
}
