package com.pwc.repository;

import com.pwc.model.Assignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    // Find assignments by organization type (through organizationDetail.organizationType)
    @Query("SELECT a FROM Assignment a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH a.organizationDetail od " +
           "WHERE od.organizationType = :orgTypeName " +
           "ORDER BY a.id DESC")
    List<Assignment> findByOrganizationType(@Param("orgTypeName") String orgTypeName);
    
    // Paginated find by organization type with search
    @Query("SELECT a FROM Assignment a " +
           "JOIN a.employee e " +
           "JOIN a.organizationDetail od " +
           "WHERE od.organizationType = :orgTypeName " +
           "AND (LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(od.organization) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "  OR LOWER(od.legacyOrganizationName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Assignment> findByOrganizationTypeWithSearch(
            @Param("orgTypeName") String orgTypeName,
            @Param("search") String search,
            Pageable pageable);
    
    // Paginated find by organization type without search
    @Query("SELECT a FROM Assignment a " +
           "JOIN a.employee e " +
           "JOIN a.organizationDetail od " +
           "WHERE od.organizationType = :orgTypeName")
    Page<Assignment> findByOrganizationTypePageable(
            @Param("orgTypeName") String orgTypeName,
            Pageable pageable);
    
    // Find by employee and organization detail (for duplicate checking)
    Optional<Assignment> findByEmployeeIdAndOrganizationDetailId(Long employeeId, Long organizationDetailId);
    
    // Find all assignments for a specific organization detail
    List<Assignment> findByOrganizationDetailId(Long organizationDetailId);
    
    // Find all assignments for a specific employee
    List<Assignment> findByEmployeeId(Long employeeId);
    
    // Count assignments by organization type
    @Query("SELECT COUNT(a) FROM Assignment a " +
           "JOIN a.organizationDetail od " +
           "WHERE od.organizationType = :orgTypeName")
    Long countByOrganizationType(@Param("orgTypeName") String orgTypeName);
    
    // Check if assignment exists
    boolean existsByEmployeeIdAndOrganizationDetailId(Long employeeId, Long organizationDetailId);
    
    // Find all assignments with employee eagerly loaded, ordered by positionId for report generation
    @Query("SELECT a FROM Assignment a " +
           "JOIN FETCH a.employee e " +
           "JOIN FETCH a.organizationDetail od " +
           "ORDER BY e.positionId ASC, a.createdAt ASC")
    List<Assignment> findAllForReport();
}
