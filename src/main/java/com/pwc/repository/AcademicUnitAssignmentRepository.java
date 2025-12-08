package com.pwc.repository;

import com.pwc.model.AcademicUnitAssignment;
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
public interface AcademicUnitAssignmentRepository extends JpaRepository<AcademicUnitAssignment, Long> {
    
    List<AcademicUnitAssignment> findByUser(User user);
    
    List<AcademicUnitAssignment> findByOrganizationDetail(OrganizationDetail organizationDetail);
    
    Optional<AcademicUnitAssignment> findByUserAndOrganizationDetail(User user, OrganizationDetail organizationDetail);
    
    boolean existsByUserAndOrganizationDetail(User user, OrganizationDetail organizationDetail);
    
    @Query("SELECT aua FROM AcademicUnitAssignment aua WHERE " +
           "LOWER(aua.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.user.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.organizationDetail.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.organizationDetail.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(aua.organizationDetail.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<AcademicUnitAssignment> searchAcademicUnitAssignments(String search, Pageable pageable);
    
    @Query("SELECT aua FROM AcademicUnitAssignment aua WHERE aua.user.id = :userId")
    Page<AcademicUnitAssignment> findByUserId(Long userId, Pageable pageable);
}



