package com.pwc.service;

import com.pwc.dto.*;
import com.pwc.model.CompanyAssignment;
import com.pwc.model.OrganizationDetail;
import com.pwc.model.User;
import com.pwc.repository.CompanyAssignmentRepository;
import com.pwc.repository.OrganizationDetailRepository;
import com.pwc.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyAssignmentService {
    
    private final CompanyAssignmentRepository companyAssignmentRepository;
    private final UserRepository userRepository;
    private final OrganizationDetailRepository organizationDetailRepository;
    
    public CompanyAssignmentService(
            CompanyAssignmentRepository companyAssignmentRepository,
            UserRepository userRepository,
            OrganizationDetailRepository organizationDetailRepository) {
        this.companyAssignmentRepository = companyAssignmentRepository;
        this.userRepository = userRepository;
        this.organizationDetailRepository = organizationDetailRepository;
    }
    
    public PageResponse<CompanyAssignmentDTO> getAllCompanyAssignments(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<CompanyAssignment> assignmentPage;
        if (search != null && !search.trim().isEmpty()) {
            assignmentPage = companyAssignmentRepository.searchCompanyAssignments(search.trim(), pageable);
        } else {
            assignmentPage = companyAssignmentRepository.findAll(pageable);
        }
        
        List<CompanyAssignmentDTO> content = assignmentPage.getContent().stream()
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
    
    public PageResponse<CompanyAssignmentDTO> getCompanyAssignmentsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyAssignment> assignmentPage = companyAssignmentRepository.findByUserId(userId, pageable);
        
        List<CompanyAssignmentDTO> content = assignmentPage.getContent().stream()
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
    
    public CompanyAssignmentDTO getCompanyAssignmentById(Long id) {
        CompanyAssignment assignment = companyAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company assignment not found"));
        return convertToDTO(assignment);
    }
    
    @Transactional
    public CompanyAssignmentDTO createCompanyAssignment(CompanyAssignmentCreateDTO createDTO) {
        User user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(createDTO.getOrganizationDetailId())
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        // Validate organization type
        if (!"Company".equalsIgnoreCase(organizationDetail.getOrganizationType())) {
            throw new RuntimeException("Organization detail must be of type 'Company'");
        }
        
        // Check for duplicate
        if (companyAssignmentRepository.existsByUserAndOrganizationDetail(user, organizationDetail)) {
            throw new RuntimeException("Company assignment already exists for this user and organization");
        }
        
        CompanyAssignment assignment = new CompanyAssignment();
        assignment.setUser(user);
        assignment.setOrganizationDetail(organizationDetail);
        
        CompanyAssignment saved = companyAssignmentRepository.save(assignment);
        return convertToDTO(saved);
    }
    
    @Transactional
    public CompanyAssignmentDTO updateCompanyAssignment(Long id, CompanyAssignmentUpdateDTO updateDTO) {
        CompanyAssignment assignment = companyAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company assignment not found"));
        
        // Company assignments don't have additional fields to update for now
        // This method is kept for consistency and future extensibility
        
        CompanyAssignment updated = companyAssignmentRepository.save(assignment);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteCompanyAssignment(Long id) {
        if (!companyAssignmentRepository.existsById(id)) {
            throw new RuntimeException("Company assignment not found");
        }
        companyAssignmentRepository.deleteById(id);
    }
    
    public List<CompanyAssignmentDTO> getCompanyAssignmentsByOrganizationDetail(Long organizationDetailId) {
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(organizationDetailId)
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        List<CompanyAssignment> assignments = companyAssignmentRepository.findByOrganizationDetail(organizationDetail);
        return assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private CompanyAssignmentDTO convertToDTO(CompanyAssignment assignment) {
        CompanyAssignmentDTO dto = new CompanyAssignmentDTO();
        dto.setId(assignment.getId());
        
        User user = assignment.getUser();
        dto.setUserId(user.getId());
        dto.setUserFirstName(user.getFirstName());
        dto.setUserLastName(user.getLastName());
        dto.setUserEmail(user.getEmail());
        dto.setUserEmployeeId(user.getEmployeeId());
        
        OrganizationDetail orgDetail = assignment.getOrganizationDetail();
        dto.setOrganizationDetailId(orgDetail.getId());
        dto.setOrganizationName(orgDetail.getOrganization());
        dto.setOrganizationType(orgDetail.getOrganizationType());
        dto.setReferenceId(orgDetail.getReferenceId());
        
        return dto;
    }
}



