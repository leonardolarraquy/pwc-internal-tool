package com.pwc.service;

import com.pwc.dto.*;
import com.pwc.model.*;
import com.pwc.repository.*;
import com.pwc.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AssignmentService {
    
    private final AssignmentRepository assignmentRepository;
    private final AssignmentFieldValueRepository fieldValueRepository;
    private final AssignmentFieldDefinitionRepository fieldDefinitionRepository;
    private final OrganizationTypeRepository orgTypeRepository;
    private final EmployeeRepository employeeRepository;
    private final OrganizationDetailRepository orgDetailRepository;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;
    
    public AssignmentService(AssignmentRepository assignmentRepository,
                            AssignmentFieldValueRepository fieldValueRepository,
                            AssignmentFieldDefinitionRepository fieldDefinitionRepository,
                            OrganizationTypeRepository orgTypeRepository,
                            EmployeeRepository employeeRepository,
                            OrganizationDetailRepository orgDetailRepository,
                            UserRepository userRepository,
                            SecurityUtil securityUtil) {
        this.assignmentRepository = assignmentRepository;
        this.fieldValueRepository = fieldValueRepository;
        this.fieldDefinitionRepository = fieldDefinitionRepository;
        this.orgTypeRepository = orgTypeRepository;
        this.employeeRepository = employeeRepository;
        this.orgDetailRepository = orgDetailRepository;
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
    }
    
    public PageResponse<AssignmentDTO> getAssignmentsByOrgType(String orgTypeSlug, int page, int size, 
                                                                String sortBy, String sortDir, String search) {
        // Get org type to find the name
        OrganizationType orgType = orgTypeRepository.findBySlug(orgTypeSlug)
                .orElseThrow(() -> new RuntimeException("Organization type not found: " + orgTypeSlug));
        
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Assignment> assignmentPage;
        if (search != null && !search.trim().isEmpty()) {
            assignmentPage = assignmentRepository.findByOrganizationTypeWithSearch(
                    orgType.getName(), search.trim(), pageable);
        } else {
            assignmentPage = assignmentRepository.findByOrganizationTypePageable(
                    orgType.getName(), pageable);
        }
        
        // Get all assignment IDs to batch fetch field values
        List<Long> assignmentIds = assignmentPage.getContent().stream()
                .map(Assignment::getId)
                .collect(Collectors.toList());
        
        // Batch fetch field values
        Map<Long, Map<String, Boolean>> fieldValuesMap = new HashMap<>();
        if (!assignmentIds.isEmpty()) {
            List<AssignmentFieldValue> allFieldValues = fieldValueRepository.findByAssignmentIdIn(assignmentIds);
            for (AssignmentFieldValue fv : allFieldValues) {
                fieldValuesMap.computeIfAbsent(fv.getAssignment().getId(), k -> new HashMap<>())
                        .put(fv.getFieldDefinition().getFieldKey(), fv.getValue());
            }
        }
        
        List<AssignmentDTO> content = assignmentPage.getContent().stream()
                .map(a -> convertToDTO(a, fieldValuesMap.getOrDefault(a.getId(), new HashMap<>())))
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                assignmentPage.getNumber(),
                assignmentPage.getSize(),
                assignmentPage.getTotalElements(),
                assignmentPage.getTotalPages(),
                assignmentPage.isLast()
        );
    }
    
    public AssignmentDTO getAssignmentById(Long id) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        List<AssignmentFieldValue> fieldValues = fieldValueRepository.findByAssignmentIdWithFieldDefinition(id);
        Map<String, Boolean> fieldValuesMap = fieldValues.stream()
                .collect(Collectors.toMap(
                        fv -> fv.getFieldDefinition().getFieldKey(),
                        AssignmentFieldValue::getValue,
                        (v1, v2) -> v1
                ));
        
        return convertToDTO(assignment, fieldValuesMap);
    }
    
    public List<AssignmentDTO> getAssignmentsByOrgDetailId(Long orgDetailId) {
        List<Assignment> assignments = assignmentRepository.findByOrganizationDetailId(orgDetailId);
        
        List<Long> assignmentIds = assignments.stream()
                .map(Assignment::getId)
                .collect(Collectors.toList());
        
        Map<Long, Map<String, Boolean>> fieldValuesMap = new HashMap<>();
        if (!assignmentIds.isEmpty()) {
            List<AssignmentFieldValue> allFieldValues = fieldValueRepository.findByAssignmentIdIn(assignmentIds);
            for (AssignmentFieldValue fv : allFieldValues) {
                fieldValuesMap.computeIfAbsent(fv.getAssignment().getId(), k -> new HashMap<>())
                        .put(fv.getFieldDefinition().getFieldKey(), fv.getValue());
            }
        }
        
        return assignments.stream()
                .map(a -> convertToDTO(a, fieldValuesMap.getOrDefault(a.getId(), new HashMap<>())))
                .collect(Collectors.toList());
    }
    
    @Transactional
    public AssignmentDTO createAssignment(AssignmentCreateDTO createDTO) {
        // Check for duplicate
        if (assignmentRepository.existsByEmployeeIdAndOrganizationDetailId(
                createDTO.getEmployeeId(), createDTO.getOrganizationDetailId())) {
            throw new RuntimeException("Assignment already exists for this employee and organization");
        }
        
        Employee employee = employeeRepository.findById(createDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        OrganizationDetail orgDetail = orgDetailRepository.findById(createDTO.getOrganizationDetailId())
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        User currentUser = securityUtil.getCurrentUser();
        
        Assignment assignment = new Assignment(employee, orgDetail, currentUser);
        Assignment saved = assignmentRepository.save(assignment);
        
        // Get org type for field definitions
        OrganizationType orgType = orgTypeRepository.findByName(orgDetail.getOrganizationType())
                .orElse(null);
        
        // Save field values
        Map<String, Boolean> savedFieldValues = new HashMap<>();
        if (orgType != null && createDTO.getFieldValues() != null) {
            List<AssignmentFieldDefinition> fieldDefs = fieldDefinitionRepository
                    .findActiveByOrganizationTypeId(orgType.getId());
            
            for (AssignmentFieldDefinition fieldDef : fieldDefs) {
                Boolean value = createDTO.getFieldValues().getOrDefault(fieldDef.getFieldKey(), false);
                AssignmentFieldValue fieldValue = new AssignmentFieldValue(saved, fieldDef, value);
                fieldValueRepository.save(fieldValue);
                savedFieldValues.put(fieldDef.getFieldKey(), value);
            }
        }
        
        return convertToDTO(saved, savedFieldValues);
    }
    
    @Transactional
    public AssignmentDTO updateAssignment(Long id, AssignmentUpdateDTO updateDTO) {
        Assignment assignment = assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        OrganizationDetail orgDetail = assignment.getOrganizationDetail();
        OrganizationType orgType = orgTypeRepository.findByName(orgDetail.getOrganizationType())
                .orElse(null);
        
        Map<String, Boolean> savedFieldValues = new HashMap<>();
        
        if (orgType != null && updateDTO.getFieldValues() != null) {
            List<AssignmentFieldDefinition> fieldDefs = fieldDefinitionRepository
                    .findActiveByOrganizationTypeId(orgType.getId());
            
            for (AssignmentFieldDefinition fieldDef : fieldDefs) {
                Boolean value = updateDTO.getFieldValues().getOrDefault(fieldDef.getFieldKey(), false);
                
                // Find or create field value
                Optional<AssignmentFieldValue> existingFv = fieldValueRepository
                        .findByAssignmentIdAndFieldDefinitionId(id, fieldDef.getId());
                
                if (existingFv.isPresent()) {
                    AssignmentFieldValue fv = existingFv.get();
                    fv.setValue(value);
                    fieldValueRepository.save(fv);
                } else {
                    AssignmentFieldValue newFv = new AssignmentFieldValue(assignment, fieldDef, value);
                    fieldValueRepository.save(newFv);
                }
                savedFieldValues.put(fieldDef.getFieldKey(), value);
            }
        }
        
        return convertToDTO(assignment, savedFieldValues);
    }
    
    @Transactional
    public void deleteAssignment(Long id) {
        if (!assignmentRepository.existsById(id)) {
            throw new RuntimeException("Assignment not found");
        }
        // Field values will be deleted by cascade
        assignmentRepository.deleteById(id);
    }
    
    public Long countByOrgType(String orgTypeName) {
        return assignmentRepository.countByOrganizationType(orgTypeName);
    }
    
    public Map<Long, Long> getAssignmentCountsByOrgType() {
        // Get all active organization types and count assignments for each
        List<OrganizationType> orgTypes = orgTypeRepository.findAllActive();
        Map<Long, Long> counts = new HashMap<>();
        
        for (OrganizationType orgType : orgTypes) {
            Long count = assignmentRepository.countByOrganizationType(orgType.getName());
            counts.put(orgType.getId(), count);
        }
        
        return counts;
    }
    
    private AssignmentDTO convertToDTO(Assignment assignment, Map<String, Boolean> fieldValues) {
        AssignmentDTO dto = new AssignmentDTO();
        dto.setId(assignment.getId());
        
        Employee emp = assignment.getEmployee();
        dto.setEmployeeId(emp.getId());
        dto.setEmployeeFirstName(emp.getFirstName());
        dto.setEmployeeLastName(emp.getLastName());
        dto.setEmployeeEmail(emp.getEmail());
        dto.setEmployeeWorkerId(emp.getEmployeeId());
        dto.setEmployeePositionId(emp.getPositionId());
        
        OrganizationDetail od = assignment.getOrganizationDetail();
        dto.setOrganizationDetailId(od.getId());
        dto.setOrganizationName(od.getOrganization() != null ? od.getOrganization() : od.getLegacyOrganizationName());
        dto.setOrganizationType(od.getOrganizationType());
        dto.setReferenceId(od.getReferenceId());
        
        User createdBy = assignment.getCreatedBy();
        dto.setCreatedById(createdBy.getId());
        dto.setCreatedByName(createdBy.getFirstName() + " " + createdBy.getLastName());
        
        dto.setCreatedAt(assignment.getCreatedAt());
        dto.setFieldValues(fieldValues);
        
        return dto;
    }
}
