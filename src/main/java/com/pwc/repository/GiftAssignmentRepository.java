package com.pwc.repository;

import com.pwc.model.Employee;
import com.pwc.model.GiftAssignment;
import com.pwc.model.OrganizationDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiftAssignmentRepository extends JpaRepository<GiftAssignment, Long> {
    
    List<GiftAssignment> findByEmployee(Employee employee);
    
    List<GiftAssignment> findByOrganizationDetail(OrganizationDetail organizationDetail);
    
    Optional<GiftAssignment> findByEmployeeAndOrganizationDetail(Employee employee, OrganizationDetail organizationDetail);
    
    boolean existsByEmployeeAndOrganizationDetail(Employee employee, OrganizationDetail organizationDetail);
    
    @Query("SELECT ga FROM GiftAssignment ga WHERE " +
           "LOWER(ga.employee.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.employee.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.employee.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.employee.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.organizationDetail.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.organizationDetail.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.organizationDetail.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<GiftAssignment> searchGiftAssignments(String search, Pageable pageable);
    
    @Query("SELECT ga FROM GiftAssignment ga WHERE ga.employee.id = :employeeId")
    Page<GiftAssignment> findByEmployeeId(Long employeeId, Pageable pageable);
    
    long countByCreatedById(Long userId);
}






