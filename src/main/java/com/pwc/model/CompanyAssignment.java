package com.pwc.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_assignments")
public class CompanyAssignment extends BaseAssignment {
    
    public CompanyAssignment() {
        super();
    }
    
    public CompanyAssignment(Employee employee, OrganizationDetail organizationDetail, User createdBy) {
        super();
        setEmployee(employee);
        setOrganizationDetail(organizationDetail);
        setCreatedBy(createdBy);
    }
}






