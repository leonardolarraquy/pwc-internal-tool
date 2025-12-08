package com.pwc.service;

import com.pwc.dto.*;
import com.pwc.model.GiftAssignment;
import com.pwc.model.OrganizationDetail;
import com.pwc.model.User;
import com.pwc.repository.GiftAssignmentRepository;
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
public class GiftAssignmentService {
    
    private final GiftAssignmentRepository giftAssignmentRepository;
    private final UserRepository userRepository;
    private final OrganizationDetailRepository organizationDetailRepository;
    
    public GiftAssignmentService(
            GiftAssignmentRepository giftAssignmentRepository,
            UserRepository userRepository,
            OrganizationDetailRepository organizationDetailRepository) {
        this.giftAssignmentRepository = giftAssignmentRepository;
        this.userRepository = userRepository;
        this.organizationDetailRepository = organizationDetailRepository;
    }
    
    public PageResponse<GiftAssignmentDTO> getAllGiftAssignments(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<GiftAssignment> assignmentPage;
        if (search != null && !search.trim().isEmpty()) {
            assignmentPage = giftAssignmentRepository.searchGiftAssignments(search.trim(), pageable);
        } else {
            assignmentPage = giftAssignmentRepository.findAll(pageable);
        }
        
        List<GiftAssignmentDTO> content = assignmentPage.getContent().stream()
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
    
    public PageResponse<GiftAssignmentDTO> getGiftAssignmentsByUser(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GiftAssignment> assignmentPage = giftAssignmentRepository.findByUserId(userId, pageable);
        
        List<GiftAssignmentDTO> content = assignmentPage.getContent().stream()
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
    
    public GiftAssignmentDTO getGiftAssignmentById(Long id) {
        GiftAssignment assignment = giftAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gift assignment not found"));
        return convertToDTO(assignment);
    }
    
    @Transactional
    public GiftAssignmentDTO createGiftAssignment(GiftAssignmentCreateDTO createDTO) {
        User user = userRepository.findById(createDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(createDTO.getOrganizationDetailId())
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        // Validate organization type
        if (!"Gift".equalsIgnoreCase(organizationDetail.getOrganizationType())) {
            throw new RuntimeException("Organization detail must be of type 'Gift'");
        }
        
        // Check for duplicate
        if (giftAssignmentRepository.existsByUserAndOrganizationDetail(user, organizationDetail)) {
            throw new RuntimeException("Gift assignment already exists for this user and organization");
        }
        
        GiftAssignment assignment = new GiftAssignment();
        assignment.setUser(user);
        assignment.setOrganizationDetail(organizationDetail);
        assignment.setFinGiftFinancialAnalyst(createDTO.getFinGiftFinancialAnalyst());
        assignment.setFinGiftManager(createDTO.getFinGiftManager());
        assignment.setFinProfessorshipPartnerGift(createDTO.getFinProfessorshipPartnerGift());
        
        GiftAssignment saved = giftAssignmentRepository.save(assignment);
        return convertToDTO(saved);
    }
    
    @Transactional
    public GiftAssignmentDTO updateGiftAssignment(Long id, GiftAssignmentUpdateDTO updateDTO) {
        GiftAssignment assignment = giftAssignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gift assignment not found"));
        
        if (updateDTO.getFinGiftFinancialAnalyst() != null) {
            assignment.setFinGiftFinancialAnalyst(updateDTO.getFinGiftFinancialAnalyst());
        }
        if (updateDTO.getFinGiftManager() != null) {
            assignment.setFinGiftManager(updateDTO.getFinGiftManager());
        }
        if (updateDTO.getFinProfessorshipPartnerGift() != null) {
            assignment.setFinProfessorshipPartnerGift(updateDTO.getFinProfessorshipPartnerGift());
        }
        
        GiftAssignment updated = giftAssignmentRepository.save(assignment);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteGiftAssignment(Long id) {
        if (!giftAssignmentRepository.existsById(id)) {
            throw new RuntimeException("Gift assignment not found");
        }
        giftAssignmentRepository.deleteById(id);
    }
    
    public List<GiftAssignmentDTO> getGiftAssignmentsByOrganizationDetail(Long organizationDetailId) {
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(organizationDetailId)
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        List<GiftAssignment> assignments = giftAssignmentRepository.findByOrganizationDetail(organizationDetail);
        return assignments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    private GiftAssignmentDTO convertToDTO(GiftAssignment assignment) {
        GiftAssignmentDTO dto = new GiftAssignmentDTO();
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
        
        dto.setFinGiftFinancialAnalyst(assignment.getFinGiftFinancialAnalyst());
        dto.setFinGiftManager(assignment.getFinGiftManager());
        dto.setFinProfessorshipPartnerGift(assignment.getFinProfessorshipPartnerGift());
        
        return dto;
    }
}



