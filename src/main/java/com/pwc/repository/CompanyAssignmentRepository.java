package com.pwc.repository;

import com.pwc.model.CompanyAssignment;
import com.pwc.model.OrganizationDetail;
import com.pwc.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyAssignmentRepository extends JpaRepository<CompanyAssignment, Long> {
    
    List<CompanyAssignment> findByUser(User user);
    
    List<CompanyAssignment> findByOrganizationDetail(OrganizationDetail organizationDetail);
    
    Optional<CompanyAssignment> findByUserAndOrganizationDetail(User user, OrganizationDetail organizationDetail);
    
    boolean existsByUserAndOrganizationDetail(User user, OrganizationDetail organizationDetail);
    
    @Query("SELECT ca FROM CompanyAssignment ca WHERE " +
           "LOWER(ca.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.user.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.organizationDetail.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.organizationDetail.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ca.organizationDetail.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<CompanyAssignment> searchCompanyAssignments(String search, Pageable pageable);
    
    @Query("SELECT ca FROM CompanyAssignment ca WHERE ca.user.id = :userId")
    Page<CompanyAssignment> findByUserId(Long userId, Pageable pageable);
}



