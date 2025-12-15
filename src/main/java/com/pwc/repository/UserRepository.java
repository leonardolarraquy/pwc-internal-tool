package com.pwc.repository;

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
    
    boolean existsByEmail(String email);
    
    long countByRole(Role role);
    
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.company) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<User> searchUsers(String search, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.company) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:accessFilter = 'all' OR " +
           "(:accessFilter = 'company' AND COALESCE(u.companyAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'academic' AND COALESCE(u.academicUnitAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'gift' AND COALESCE(u.giftAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'location' AND COALESCE(u.locationAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'project' AND COALESCE(u.projectAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'grant' AND COALESCE(u.grantAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'paygroup' AND COALESCE(u.paygroupAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'none' AND COALESCE(u.companyAssignmentsAccess, false) = false AND " +
           "COALESCE(u.academicUnitAssignmentsAccess, false) = false AND " +
           "COALESCE(u.giftAssignmentsAccess, false) = false AND " +
           "COALESCE(u.locationAssignmentsAccess, false) = false AND " +
           "COALESCE(u.projectAssignmentsAccess, false) = false AND " +
           "COALESCE(u.grantAssignmentsAccess, false) = false AND " +
           "COALESCE(u.paygroupAssignmentsAccess, false) = false))")
    Page<User> searchUsersWithAccessFilter(String search, String accessFilter, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:accessFilter = 'all' OR " +
           "(:accessFilter = 'company' AND COALESCE(u.companyAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'academic' AND COALESCE(u.academicUnitAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'gift' AND COALESCE(u.giftAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'location' AND COALESCE(u.locationAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'project' AND COALESCE(u.projectAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'grant' AND COALESCE(u.grantAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'paygroup' AND COALESCE(u.paygroupAssignmentsAccess, false) = true) OR " +
           "(:accessFilter = 'none' AND COALESCE(u.companyAssignmentsAccess, false) = false AND " +
           "COALESCE(u.academicUnitAssignmentsAccess, false) = false AND " +
           "COALESCE(u.giftAssignmentsAccess, false) = false AND " +
           "COALESCE(u.locationAssignmentsAccess, false) = false AND " +
           "COALESCE(u.projectAssignmentsAccess, false) = false AND " +
           "COALESCE(u.grantAssignmentsAccess, false) = false AND " +
           "COALESCE(u.paygroupAssignmentsAccess, false) = false))")
    Page<User> findAllWithAccessFilter(String accessFilter, Pageable pageable);
}
