package com.pwc.repository;

import com.pwc.model.AssignmentFieldDefinition;
import com.pwc.model.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentFieldDefinitionRepository extends JpaRepository<AssignmentFieldDefinition, Long> {
    
    @Query("SELECT afd FROM AssignmentFieldDefinition afd WHERE afd.organizationType.id = :orgTypeId AND afd.active = true ORDER BY afd.displayOrder ASC")
    List<AssignmentFieldDefinition> findActiveByOrganizationTypeId(@Param("orgTypeId") Long orgTypeId);
    
    @Query("SELECT afd FROM AssignmentFieldDefinition afd WHERE afd.organizationType.id = :orgTypeId ORDER BY afd.displayOrder ASC")
    List<AssignmentFieldDefinition> findAllByOrganizationTypeId(@Param("orgTypeId") Long orgTypeId);
    
    @Query("SELECT afd FROM AssignmentFieldDefinition afd WHERE afd.organizationType.slug = :slug AND afd.active = true ORDER BY afd.displayOrder ASC")
    List<AssignmentFieldDefinition> findActiveByOrganizationTypeSlug(@Param("slug") String slug);
    
    Optional<AssignmentFieldDefinition> findByOrganizationTypeAndFieldKey(OrganizationType organizationType, String fieldKey);
    
    boolean existsByOrganizationTypeIdAndFieldKey(Long orgTypeId, String fieldKey);
}
