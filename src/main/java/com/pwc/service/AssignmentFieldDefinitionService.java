package com.pwc.service;

import com.pwc.dto.*;
import com.pwc.model.AssignmentFieldDefinition;
import com.pwc.model.OrganizationType;
import com.pwc.repository.AssignmentFieldDefinitionRepository;
import com.pwc.repository.OrganizationTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentFieldDefinitionService {
    
    private final AssignmentFieldDefinitionRepository repository;
    private final OrganizationTypeRepository orgTypeRepository;
    
    public AssignmentFieldDefinitionService(AssignmentFieldDefinitionRepository repository,
                                            OrganizationTypeRepository orgTypeRepository) {
        this.repository = repository;
        this.orgTypeRepository = orgTypeRepository;
    }
    
    public List<AssignmentFieldDefinitionDTO> getFieldDefinitionsByOrgTypeId(Long orgTypeId) {
        return repository.findAllByOrganizationTypeId(orgTypeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AssignmentFieldDefinitionDTO> getActiveFieldDefinitionsByOrgTypeId(Long orgTypeId) {
        return repository.findActiveByOrganizationTypeId(orgTypeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AssignmentFieldDefinitionDTO> getActiveFieldDefinitionsByOrgTypeSlug(String slug) {
        return repository.findActiveByOrganizationTypeSlug(slug).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public AssignmentFieldDefinitionDTO getFieldDefinitionById(Long id) {
        AssignmentFieldDefinition fieldDef = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field definition not found"));
        return convertToDTO(fieldDef);
    }
    
    @Transactional
    public AssignmentFieldDefinitionDTO createFieldDefinition(AssignmentFieldDefinitionCreateDTO createDTO) {
        OrganizationType orgType = orgTypeRepository.findById(createDTO.getOrganizationTypeId())
                .orElseThrow(() -> new RuntimeException("Organization type not found"));
        
        if (repository.existsByOrganizationTypeIdAndFieldKey(createDTO.getOrganizationTypeId(), createDTO.getFieldKey())) {
            throw new RuntimeException("Field with this key already exists for this organization type");
        }
        
        AssignmentFieldDefinition fieldDef = new AssignmentFieldDefinition();
        fieldDef.setOrganizationType(orgType);
        fieldDef.setFieldKey(createDTO.getFieldKey());
        fieldDef.setFieldTitle(createDTO.getFieldTitle());
        fieldDef.setFieldDescription(createDTO.getFieldDescription());
        fieldDef.setDisplayOrder(createDTO.getDisplayOrder() != null ? createDTO.getDisplayOrder() : 0);
        fieldDef.setActive(true);
        
        AssignmentFieldDefinition saved = repository.save(fieldDef);
        return convertToDTO(saved);
    }
    
    @Transactional
    public AssignmentFieldDefinitionDTO updateFieldDefinition(Long id, AssignmentFieldDefinitionUpdateDTO updateDTO) {
        AssignmentFieldDefinition fieldDef = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field definition not found"));
        
        if (updateDTO.getFieldKey() != null) {
            // Check if key is being changed and doesn't conflict
            if (!updateDTO.getFieldKey().equals(fieldDef.getFieldKey())) {
                if (repository.existsByOrganizationTypeIdAndFieldKey(
                        fieldDef.getOrganizationType().getId(), updateDTO.getFieldKey())) {
                    throw new RuntimeException("Field with this key already exists for this organization type");
                }
            }
            fieldDef.setFieldKey(updateDTO.getFieldKey());
        }
        if (updateDTO.getFieldTitle() != null) {
            fieldDef.setFieldTitle(updateDTO.getFieldTitle());
        }
        if (updateDTO.getFieldDescription() != null) {
            fieldDef.setFieldDescription(updateDTO.getFieldDescription());
        }
        if (updateDTO.getDisplayOrder() != null) {
            fieldDef.setDisplayOrder(updateDTO.getDisplayOrder());
        }
        if (updateDTO.getActive() != null) {
            fieldDef.setActive(updateDTO.getActive());
        }
        
        AssignmentFieldDefinition updated = repository.save(fieldDef);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteFieldDefinition(Long id) {
        AssignmentFieldDefinition fieldDef = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field definition not found"));
        // Soft delete
        fieldDef.setActive(false);
        repository.save(fieldDef);
    }
    
    @Transactional
    public void hardDeleteFieldDefinition(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Field definition not found");
        }
        repository.deleteById(id);
    }
    
    private AssignmentFieldDefinitionDTO convertToDTO(AssignmentFieldDefinition fieldDef) {
        return new AssignmentFieldDefinitionDTO(
                fieldDef.getId(),
                fieldDef.getOrganizationType().getId(),
                fieldDef.getOrganizationType().getName(),
                fieldDef.getFieldKey(),
                fieldDef.getFieldTitle(),
                fieldDef.getFieldDescription(),
                fieldDef.getDisplayOrder(),
                fieldDef.getActive()
        );
    }
}
