package com.pwc.service;

import com.pwc.dto.*;
import com.pwc.model.AcademicUnitAssignment;
import com.pwc.model.Employee;
import com.pwc.model.OrganizationDetail;
import com.pwc.model.User;
import com.pwc.repository.AcademicUnitAssignmentRepository;
import com.pwc.repository.EmployeeRepository;
import com.pwc.repository.OrganizationDetailRepository;
import com.pwc.util.SecurityUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AcademicUnitAssignmentService {
    
    private final AcademicUnitAssignmentRepository academicUnitAssignmentRepository;
    private final EmployeeRepository employeeRepository;
    private final OrganizationDetailRepository organizationDetailRepository;
    private final SecurityUtil securityUtil;
    
    public AcademicUnitAssignmentService(
            AcademicUnitAssignmentRepository academicUnitAssignmentRepository,
            EmployeeRepository employeeRepository,
            OrganizationDetailRepository organizationDetailRepository,
            SecurityUtil securityUtil) {
        this.academicUnitAssignmentRepository = academicUnitAssignmentRepository;
        this.employeeRepository = employeeRepository;
        this.organizationDetailRepository = organizationDetailRepository;
        this.securityUtil = securityUtil;
    }
    
    public PageResponse<AcademicUnitAssignmentDTO> getAllAcademicUnitAssignments(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AcademicUnitAssignment> assignmentPage;
        if (search != null && !search.trim().isEmpty()) {
            assignmentPage = academicUnitAssignmentRepository.searchAcademicUnitAssignments(search.trim(), pageable);
        } else {
            assignmentPage = academicUnitAssignmentRepository.findAll(pageable);
        }
        
        List<AcademicUnitAssignmentDTO> content = assignmentPage.getContent().stream()
                .map(this::convertToDTO)
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
    
    public PageResponse<AcademicUnitAssignmentDTO> getAcademicUnitAssignmentsByEmployee(Long employeeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AcademicUnitAssignment> assignmentPage = academicUnitAssignmentRepository.findByEmployeeId(employeeId, pageable);
        
        List<AcademicUnitAssignmentDTO> content = assignmentPage.getContent().stream()
                .map(this::convertToDTO)
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
    
    public AcademicUnitAssignmentDTO getAcademicUnitAssignmentById(Long id) {
        AcademicUnitAssignment assignment = academicUnitAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Academic unit assignment not found"));
        return convertToDTO(assignment);
    }
    
    @Transactional
    public AcademicUnitAssignmentDTO createAcademicUnitAssignment(AcademicUnitAssignmentCreateDTO createDTO) {
        Employee employee = employeeRepository.findById(createDTO.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(createDTO.getOrganizationDetailId())
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        // Validate organization type
        if (!"Academic Unit".equalsIgnoreCase(organizationDetail.getOrganizationType())) {
            throw new RuntimeException("Organization detail must be of type 'Academic Unit'");
        }
        
        // Check for duplicate
        if (academicUnitAssignmentRepository.existsByEmployeeAndOrganizationDetail(employee, organizationDetail)) {
            throw new RuntimeException("Academic unit assignment already exists for this employee and organization");
        }
        
        User currentUser = securityUtil.getCurrentUser();
        AcademicUnitAssignment assignment = new AcademicUnitAssignment(employee, organizationDetail, currentUser);
        assignment.setHcmAcademicChairAu(createDTO.getHcmAcademicChairAu());
        assignment.setHcmAcademicDeanAuh(createDTO.getHcmAcademicDeanAuh());
        assignment.setHcmAcademicFacultyExecutiveAuh(createDTO.getHcmAcademicFacultyExecutiveAuh());
        assignment.setHcmAcademicFacultyHrAnalystAu(createDTO.getHcmAcademicFacultyHrAnalystAu());
        assignment.setHcmAcademicProvostPartnerAuh(createDTO.getHcmAcademicProvostPartnerAuh());
        assignment.setHcmAcademicSchoolDirectorAuh(createDTO.getHcmAcademicSchoolDirectorAuh());
        
        AcademicUnitAssignment saved = academicUnitAssignmentRepository.save(assignment);
        return convertToDTO(saved);
    }
    
    @Transactional
    public AcademicUnitAssignmentDTO updateAcademicUnitAssignment(Long id, AcademicUnitAssignmentUpdateDTO updateDTO) {
        AcademicUnitAssignment assignment = academicUnitAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Academic unit assignment not found"));
        
        if (updateDTO.getHcmAcademicChairAu() != null) {
            assignment.setHcmAcademicChairAu(updateDTO.getHcmAcademicChairAu());
        }
        if (updateDTO.getHcmAcademicDeanAuh() != null) {
            assignment.setHcmAcademicDeanAuh(updateDTO.getHcmAcademicDeanAuh());
        }
        if (updateDTO.getHcmAcademicFacultyExecutiveAuh() != null) {
            assignment.setHcmAcademicFacultyExecutiveAuh(updateDTO.getHcmAcademicFacultyExecutiveAuh());
        }
        if (updateDTO.getHcmAcademicFacultyHrAnalystAu() != null) {
            assignment.setHcmAcademicFacultyHrAnalystAu(updateDTO.getHcmAcademicFacultyHrAnalystAu());
        }
        if (updateDTO.getHcmAcademicProvostPartnerAuh() != null) {
            assignment.setHcmAcademicProvostPartnerAuh(updateDTO.getHcmAcademicProvostPartnerAuh());
        }
        if (updateDTO.getHcmAcademicSchoolDirectorAuh() != null) {
            assignment.setHcmAcademicSchoolDirectorAuh(updateDTO.getHcmAcademicSchoolDirectorAuh());
        }
        
        AcademicUnitAssignment updated = academicUnitAssignmentRepository.save(assignment);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteAcademicUnitAssignment(Long id) {
        if (!academicUnitAssignmentRepository.existsById(id)) {
            throw new RuntimeException("Academic unit assignment not found");
        }
        academicUnitAssignmentRepository.deleteById(id);
    }
    
    public List<AcademicUnitAssignmentDTO> getAcademicUnitAssignmentsByOrganizationDetail(Long organizationDetailId) {
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(organizationDetailId)
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        List<AcademicUnitAssignment> assignments = academicUnitAssignmentRepository.findByOrganizationDetail(organizationDetail);
        return assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private AcademicUnitAssignmentDTO convertToDTO(AcademicUnitAssignment assignment) {
        AcademicUnitAssignmentDTO dto = new AcademicUnitAssignmentDTO();
        dto.setId(assignment.getId());
        
        Employee employee = assignment.getEmployee();
        dto.setEmployeeId(employee.getId());
        dto.setEmployeeFirstName(employee.getFirstName());
        dto.setEmployeeLastName(employee.getLastName());
        dto.setEmployeeEmail(employee.getEmail());
        dto.setEmployeeEmployeeId(employee.getEmployeeId());
        
        OrganizationDetail orgDetail = assignment.getOrganizationDetail();
        dto.setOrganizationDetailId(orgDetail.getId());
        dto.setOrganizationName(orgDetail.getOrganization());
        dto.setOrganizationType(orgDetail.getOrganizationType());
        dto.setReferenceId(orgDetail.getReferenceId());
        
        dto.setHcmAcademicChairAu(assignment.getHcmAcademicChairAu());
        dto.setHcmAcademicDeanAuh(assignment.getHcmAcademicDeanAuh());
        dto.setHcmAcademicFacultyExecutiveAuh(assignment.getHcmAcademicFacultyExecutiveAuh());
        dto.setHcmAcademicFacultyHrAnalystAu(assignment.getHcmAcademicFacultyHrAnalystAu());
        dto.setHcmAcademicProvostPartnerAuh(assignment.getHcmAcademicProvostPartnerAuh());
        dto.setHcmAcademicSchoolDirectorAuh(assignment.getHcmAcademicSchoolDirectorAuh());
        
        return dto;
    }
}






