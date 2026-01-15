package com.pwc.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);
    
    // Dynamic access filter using organization type ID
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.organizationAccess oa " +
           "WHERE (LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.company) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:orgTypeId IS NULL OR (oa.organizationType.id = :orgTypeId AND oa.hasAccess = true))")
    Page<User> searchUsersWithOrgTypeAccess(@Param("search") String search, @Param("orgTypeId") Long orgTypeId, Pageable pageable);
    
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.organizationAccess oa " +
           "WHERE :orgTypeId IS NULL OR (oa.organizationType.id = :orgTypeId AND oa.hasAccess = true)")
    Page<User> findAllWithOrgTypeAccess(@Param("orgTypeId") Long orgTypeId, Pageable pageable);
    
    // Find users with no access to any organization type
    @Query("SELECT u FROM User u WHERE NOT EXISTS " +
           "(SELECT oa FROM UserOrganizationAccess oa WHERE oa.user = u AND oa.hasAccess = true)")
    Page<User> findUsersWithNoAccess(Pageable pageable);
    
    // Search users with no access
    @Query("SELECT u FROM User u WHERE " +
           "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(u.company) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND NOT EXISTS (SELECT oa FROM UserOrganizationAccess oa WHERE oa.user = u AND oa.hasAccess = true)")
    Page<User> searchUsersWithNoAccess(@Param("search") String search, Pageable pageable);
}
