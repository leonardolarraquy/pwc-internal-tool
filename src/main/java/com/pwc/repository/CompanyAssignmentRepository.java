package com.pwc.repository;

import com.pwc.model.CompanyAssignment;
import com.pwc.model.Employee;
import com.pwc.model.OrganizationDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyAssignmentRepository extends JpaRepository<CompanyAssignment, Long> {
    
    List<CompanyAssignment> findByEmployee(Employee employee);
    
    List<CompanyAssignment> findByOrganizationDetail(OrganizationDetail organizationDetail);
    
    Optional<CompanyAssignment> findByEmployeeAndOrganizationDetail(Employee employee, OrganizationDetail organizationDetail);
    
    boolean existsByEmployeeAndOrganizationDetail(Employee employee, OrganizationDetail organizationDetail);
    
    @Query("SELECT ca FROM CompanyAssignment ca WHERE " +
           "LOWER(ca.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.employee.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.employee.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.organizationDetail.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.organizationDetail.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.organizationDetail.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<CompanyAssignment> searchCompanyAssignments(String search, Pageable pageable);
    
    @Query("SELECT ca FROM CompanyAssignment ca WHERE ca.employee.id = :employeeId")
    Page<CompanyAssignment> findByEmployeeId(Long employeeId, Pageable pageable);
    
    long countByCreatedById(Long userId);
}






