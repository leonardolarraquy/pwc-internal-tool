package com.pwc.repository;

import com.pwc.model.AcademicUnitAssignment;
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
public interface AcademicUnitAssignmentRepository extends JpaRepository<AcademicUnitAssignment, Long> {
    
    List<AcademicUnitAssignment> findByEmployee(Employee employee);
    
    List<AcademicUnitAssignment> findByOrganizationDetail(OrganizationDetail organizationDetail);
    
    Optional<AcademicUnitAssignment> findByEmployeeAndOrganizationDetail(Employee employee, OrganizationDetail organizationDetail);
    
    boolean existsByEmployeeAndOrganizationDetail(Employee employee, OrganizationDetail organizationDetail);
    
    @Query("SELECT aua FROM AcademicUnitAssignment aua WHERE " +
           "LOWER(aua.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.employee.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.employee.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.organizationDetail.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.organizationDetail.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.organizationDetail.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<AcademicUnitAssignment> searchAcademicUnitAssignments(String search, Pageable pageable);
    
    @Query("SELECT aua FROM AcademicUnitAssignment aua WHERE aua.employee.id = :employeeId")
    Page<AcademicUnitAssignment> findByEmployeeId(Long employeeId, Pageable pageable);
    
    long countByCreatedById(Long userId);
}






