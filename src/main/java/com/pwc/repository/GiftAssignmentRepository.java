package com.pwc.repository;

import com.pwc.model.GiftAssignment;
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
public interface GiftAssignmentRepository extends JpaRepository<GiftAssignment, Long> {
    
    List<GiftAssignment> findByUser(User user);
    
    List<GiftAssignment> findByOrganizationDetail(OrganizationDetail organizationDetail);
    
    Optional<GiftAssignment> findByUserAndOrganizationDetail(User user, OrganizationDetail organizationDetail);
    
    boolean existsByUserAndOrganizationDetail(User user, OrganizationDetail organizationDetail);
    
    @Query("SELECT ga FROM GiftAssignment ga WHERE " +
           "LOWER(ga.user.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.user.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.user.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.user.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.organizationDetail.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.organizationDetail.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(ga.organizationDetail.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<GiftAssignment> searchGiftAssignments(String search, Pageable pageable);
    
    @Query("SELECT ga FROM GiftAssignment ga WHERE ga.user.id = :userId")
    Page<GiftAssignment> findByUserId(Long userId, Pageable pageable);
}



