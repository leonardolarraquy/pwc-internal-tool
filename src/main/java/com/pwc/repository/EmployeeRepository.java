package com.pwc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pwc.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    
    Optional<Employee> findByEmail(String email);
    
    List<Employee> findAllByEmployeeId(String employeeId);
    
    List<Employee> findAllByEmail(String email);
    
    List<Employee> findAllByPositionId(String positionId);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmployeeId(String employeeId);
    
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(e.employeeId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Employee> searchEmployees(String search, Pageable pageable);
    
    /**
     * Checks if an employee exists with all the same identifying fields
     * (employeeId, firstName, lastName, email, positionId, positionTitle)
     */
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE " +
           "e.employeeId = :employeeId AND " +
           "e.firstName = :firstName AND " +
           "e.lastName = :lastName AND " +
           "e.email = :email AND " +
           "((e.positionId IS NULL AND :positionId IS NULL) OR (e.positionId = :positionId)) AND " +
           "((e.positionTitle IS NULL AND :positionTitle IS NULL) OR (e.positionTitle = :positionTitle))")
    boolean existsByAllFields(String employeeId, String firstName, String lastName, String email, 
                              String positionId, String positionTitle);
    
    /**
     * Checks if an employee exists with all the same identifying fields, excluding the employee with the given ID
     */
    @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE " +
           "e.id != :excludeId AND " +
           "e.employeeId = :employeeId AND " +
           "e.firstName = :firstName AND " +
           "e.lastName = :lastName AND " +
           "e.email = :email AND " +
           "((e.positionId IS NULL AND :positionId IS NULL) OR (e.positionId = :positionId)) AND " +
           "((e.positionTitle IS NULL AND :positionTitle IS NULL) OR (e.positionTitle = :positionTitle))")
    boolean existsByAllFieldsExcludingId(Long excludeId, String employeeId, String firstName, String lastName, 
                                         String email, String positionId, String positionTitle);
    
    /**
     * Search employee by employeeId, email, or positionId (exact match)
     * Returns a list since there can be multiple matches
     */
    @Query("SELECT e FROM Employee e WHERE " +
           "e.employeeId = :query OR " +
           "LOWER(e.email) = LOWER(:query) OR " +
           "e.positionId = :query")
    List<Employee> findByEmployeeIdOrEmailOrPositionId(String query);
}

