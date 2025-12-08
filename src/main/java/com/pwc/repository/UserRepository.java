package com.pwc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pwc.model.Role;
import com.pwc.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmployeeId(String employeeId);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmployeeId(String employeeId);
    
    long countByRole(Role role);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.employeeId) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:accessFilter = 'all' OR " +
           "(:accessFilter = 'company' AND COALESCE(u.companyAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'academic' AND COALESCE(u.academicUnitAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'gift' AND COALESCE(u.giftAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'none' AND COALESCE(u.companyAssignmentsAccess, false) = false AND COALESCE(u.academicUnitAssignmentsAccess, false) = false AND COALESCE(u.giftAssignmentsAccess, false) = false))")
    Page<User> searchUsersWithAccessFilter(String search, String accessFilter, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:accessFilter = 'all' OR " +
           "(:accessFilter = 'company' AND COALESCE(u.companyAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'academic' AND COALESCE(u.academicUnitAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'gift' AND COALESCE(u.giftAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'none' AND COALESCE(u.companyAssignmentsAccess, false) = false AND COALESCE(u.academicUnitAssignmentsAccess, false) = false AND COALESCE(u.giftAssignmentsAccess, false) = false))")
    Page<User> findAllWithAccessFilter(String accessFilter, Pageable pageable);
    
    /**
     * Checks if a user exists with all the same identifying fields
     * (employeeId, firstName, lastName, email, positionId, positionTitle)
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE " +
           "u.employeeId = :employeeId AND " +
           "u.firstName = :firstName AND " +
           "u.lastName = :lastName AND " +
           "u.email = :email AND " +
           "((u.positionId IS NULL AND :positionId IS NULL) OR (u.positionId = :positionId)) AND " +
           "((u.positionTitle IS NULL AND :positionTitle IS NULL) OR (u.positionTitle = :positionTitle))")
    boolean existsByAllFields(String employeeId, String firstName, String lastName, String email, 
                              String positionId, String positionTitle);
    
    /**
     * Checks if a user exists with all the same identifying fields, excluding the user with the given ID
     * Used for update operations to allow updating the same user but prevent duplicates with others
     */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE " +
           "u.id != :excludeId AND " +
           "u.employeeId = :employeeId AND " +
           "u.firstName = :firstName AND " +
           "u.lastName = :lastName AND " +
           "u.email = :email AND " +
           "((u.positionId IS NULL AND :positionId IS NULL) OR (u.positionId = :positionId)) AND " +
           "((u.positionTitle IS NULL AND :positionTitle IS NULL) OR (u.positionTitle = :positionTitle))")
    boolean existsByAllFieldsExcludingId(Long excludeId, String employeeId, String firstName, String lastName, 
                                         String email, String positionId, String positionTitle);
    
    /**
     * Search user by employeeId, email, or positionId (exact match)
     * Returns a list since there can be multiple matches (e.g., duplicate employeeIds)
     */
    @Query("SELECT u FROM User u WHERE " +
           "u.employeeId = :query OR " +
           "LOWER(u.email) = LOWER(:query) OR " +
           "u.positionId = :query")
    List<User> findByEmployeeIdOrEmailOrPositionId(String query);
}


