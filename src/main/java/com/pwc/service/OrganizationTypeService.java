package com.pwc.service;

import com.pwc.dto.*;
import com.pwc.model.OrganizationType;
import com.pwc.repository.OrganizationTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrganizationTypeService {
    
    private final OrganizationTypeRepository repository;
    
    public OrganizationTypeService(OrganizationTypeRepository repository) {
        this.repository = repository;
    }
    
    public List<OrganizationTypeDTO> getAllOrganizationTypes() {
        return repository.findAllOrderByDisplayOrder().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<OrganizationTypeDTO> getActiveOrganizationTypes() {
        return repository.findAllActive().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public OrganizationTypeDTO getOrganizationTypeById(Long id) {
        OrganizationType orgType = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization type not found"));
        return convertToDTO(orgType);
    }
    
    public OrganizationTypeDTO getOrganizationTypeBySlug(String slug) {
        OrganizationType orgType = repository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Organization type not found: " + slug));
        return convertToDTO(orgType);
    }
    
    public OrganizationTypeDTO getOrganizationTypeByName(String name) {
        OrganizationType orgType = repository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Organization type not found: " + name));
        return convertToDTO(orgType);
    }
    
    @Transactional
    public OrganizationTypeDTO createOrganizationType(OrganizationTypeCreateDTO createDTO) {
        if (repository.existsByName(createDTO.getName())) {
            throw new RuntimeException("Organization type with this name already exists");
        }
        if (repository.existsBySlug(createDTO.getSlug())) {
            throw new RuntimeException("Organization type with this slug already exists");
        }
        
        OrganizationType orgType = new OrganizationType();
        orgType.setName(createDTO.getName());
        orgType.setSlug(createDTO.getSlug());
        orgType.setDisplayName(createDTO.getDisplayName());
        orgType.setIconName(createDTO.getIconName());
        orgType.setDisplayOrder(createDTO.getDisplayOrder() != null ? createDTO.getDisplayOrder() : 0);
        orgType.setActive(true);
        
        OrganizationType saved = repository.save(orgType);
        return convertToDTO(saved);
    }
    
    @Transactional
    public OrganizationTypeDTO updateOrganizationType(Long id, OrganizationTypeUpdateDTO updateDTO) {
        OrganizationType orgType = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization type not found"));
        
        if (updateDTO.getName() != null) {
            // Check if name is being changed and new name doesn't conflict
            if (!updateDTO.getName().equals(orgType.getName()) && repository.existsByName(updateDTO.getName())) {
                throw new RuntimeException("Organization type with this name already exists");
            }
            orgType.setName(updateDTO.getName());
        }
        if (updateDTO.getSlug() != null) {
            if (!updateDTO.getSlug().equals(orgType.getSlug()) && repository.existsBySlug(updateDTO.getSlug())) {
                throw new RuntimeException("Organization type with this slug already exists");
            }
            orgType.setSlug(updateDTO.getSlug());
        }
        if (updateDTO.getDisplayName() != null) {
            orgType.setDisplayName(updateDTO.getDisplayName());
        }
        if (updateDTO.getIconName() != null) {
            orgType.setIconName(updateDTO.getIconName());
        }
        if (updateDTO.getDisplayOrder() != null) {
            orgType.setDisplayOrder(updateDTO.getDisplayOrder());
        }
        if (updateDTO.getActive() != null) {
            orgType.setActive(updateDTO.getActive());
        }
        
        OrganizationType updated = repository.save(orgType);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteOrganizationType(Long id) {
        OrganizationType orgType = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization type not found"));
        // Soft delete by setting active to false
        orgType.setActive(false);
        repository.save(orgType);
    }
    
    private OrganizationTypeDTO convertToDTO(OrganizationType orgType) {
        return new OrganizationTypeDTO(
                orgType.getId(),
                orgType.getName(),
                orgType.getSlug(),
                orgType.getDisplayName(),
                orgType.getIconName(),
                orgType.getDisplayOrder(),
                orgType.getActive()
        );
    }
}
