package com.pwc.repository;

import com.pwc.model.AssignmentFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentFieldValueRepository extends JpaRepository<AssignmentFieldValue, Long> {
    
    @Query("SELECT afv FROM AssignmentFieldValue afv " +
           "JOIN FETCH afv.fieldDefinition fd " +
           "WHERE afv.assignment.id = :assignmentId " +
           "ORDER BY fd.displayOrder ASC")
    List<AssignmentFieldValue> findByAssignmentIdWithFieldDefinition(@Param("assignmentId") Long assignmentId);
    
    List<AssignmentFieldValue> findByAssignmentId(Long assignmentId);
    
    Optional<AssignmentFieldValue> findByAssignmentIdAndFieldDefinitionId(Long assignmentId, Long fieldDefinitionId);
    
    void deleteByAssignmentId(Long assignmentId);
    
    // Find all field values for assignments (batch query for performance)
    @Query("SELECT afv FROM AssignmentFieldValue afv " +
           "JOIN FETCH afv.fieldDefinition fd " +
           "WHERE afv.assignment.id IN :assignmentIds " +
           "ORDER BY afv.assignment.id, fd.displayOrder ASC")
    List<AssignmentFieldValue> findByAssignmentIdIn(@Param("assignmentIds") List<Long> assignmentIds);
}
