package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.OrganizationDetailService;
import com.pwc.service.GiftAssignmentService;
import com.pwc.service.AcademicUnitAssignmentService;
import com.pwc.service.CompanyAssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organization-details")
public class OrganizationDetailController {
    
    private final OrganizationDetailService organizationDetailService;
    private final GiftAssignmentService giftAssignmentService;
    private final AcademicUnitAssignmentService academicUnitAssignmentService;
    private final CompanyAssignmentService companyAssignmentService;
    
    public OrganizationDetailController(
            OrganizationDetailService organizationDetailService,
            GiftAssignmentService giftAssignmentService,
            AcademicUnitAssignmentService academicUnitAssignmentService,
            CompanyAssignmentService companyAssignmentService) {
        this.organizationDetailService = organizationDetailService;
        this.giftAssignmentService = giftAssignmentService;
        this.academicUnitAssignmentService = academicUnitAssignmentService;
        this.companyAssignmentService = companyAssignmentService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<OrganizationDetailDTO>> getAllOrganizationDetails(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String organizationTypeFilter) {
        
        PageResponse<OrganizationDetailDTO> response = organizationDetailService.getAllOrganizationDetails(page, size, sortBy, sortDir, search, organizationTypeFilter);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/organization-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrganizationTypes() {
        List<String> types = organizationDetailService.getDistinctOrganizationTypes();
        Map<String, Object> response = new HashMap<>();
        response.put("types", types);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDetailDTO> getOrganizationDetailById(@PathVariable Long id) {
        OrganizationDetailDTO organizationDetail = organizationDetailService.getOrganizationDetailById(id);
        return ResponseEntity.ok(organizationDetail);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDetailDTO> createOrganizationDetail(@Valid @RequestBody OrganizationDetailCreateDTO createDTO) {
        OrganizationDetailDTO organizationDetail = organizationDetailService.createOrganizationDetail(createDTO);
        return ResponseEntity.ok(organizationDetail);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrganizationDetailDTO> updateOrganizationDetail(@PathVariable Long id, 
                                                                          @Valid @RequestBody OrganizationDetailUpdateDTO updateDTO) {
        OrganizationDetailDTO organizationDetail = organizationDetailService.updateOrganizationDetail(id, updateDTO);
        return ResponseEntity.ok(organizationDetail);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrganizationDetail(@PathVariable Long id) {
        organizationDetailService.deleteOrganizationDetail(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importOrganizationDetails(@RequestParam("file") MultipartFile file) {
        int importedCount = organizationDetailService.importOrganizationDetailsFromCsv(file);
        Map<String, Object> response = new HashMap<>();
        response.put("imported", importedCount);
        response.put("message", "Successfully imported " + importedCount + " organization details");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/assign-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> assignUserToOrganization(
            @PathVariable Long id,
            @Valid @RequestBody AssignUserToOrganizationDTO assignDTO) {
        
        OrganizationDetailDTO orgDetail = organizationDetailService.getOrganizationDetailById(id);
        String organizationType = orgDetail.getOrganizationType();
        
        Map<String, Object> response = new HashMap<>();
        
        if ("Gift".equalsIgnoreCase(organizationType)) {
            GiftAssignmentCreateDTO createDTO = new GiftAssignmentCreateDTO();
            createDTO.setUserId(assignDTO.getUserId());
            createDTO.setOrganizationDetailId(id);
            createDTO.setFinGiftFinancialAnalyst(assignDTO.getFinGiftFinancialAnalyst());
            createDTO.setFinGiftManager(assignDTO.getFinGiftManager());
            createDTO.setFinProfessorshipPartnerGift(assignDTO.getFinProfessorshipPartnerGift());
            
            GiftAssignmentDTO assignment = giftAssignmentService.createGiftAssignment(createDTO);
            response.put("assignment", assignment);
            response.put("type", "Gift");
        } else if ("Academic Unit".equalsIgnoreCase(organizationType)) {
            AcademicUnitAssignmentCreateDTO createDTO = new AcademicUnitAssignmentCreateDTO();
            createDTO.setUserId(assignDTO.getUserId());
            createDTO.setOrganizationDetailId(id);
            createDTO.setHcmAcademicChairAu(assignDTO.getHcmAcademicChairAu());
            createDTO.setHcmAcademicDeanAuh(assignDTO.getHcmAcademicDeanAuh());
            createDTO.setHcmAcademicFacultyExecutiveAuh(assignDTO.getHcmAcademicFacultyExecutiveAuh());
            createDTO.setHcmAcademicFacultyHrAnalystAu(assignDTO.getHcmAcademicFacultyHrAnalystAu());
            createDTO.setHcmAcademicProvostPartnerAuh(assignDTO.getHcmAcademicProvostPartnerAuh());
            createDTO.setHcmAcademicSchoolDirectorAuh(assignDTO.getHcmAcademicSchoolDirectorAuh());
            
            AcademicUnitAssignmentDTO assignment = academicUnitAssignmentService.createAcademicUnitAssignment(createDTO);
            response.put("assignment", assignment);
            response.put("type", "AcademicUnit");
        } else if ("Company".equalsIgnoreCase(organizationType)) {
            CompanyAssignmentCreateDTO createDTO = new CompanyAssignmentCreateDTO();
            createDTO.setUserId(assignDTO.getUserId());
            createDTO.setOrganizationDetailId(id);
            
            CompanyAssignmentDTO assignment = companyAssignmentService.createCompanyAssignment(createDTO);
            response.put("assignment", assignment);
            response.put("type", "Company");
        } else {
            throw new RuntimeException("Cannot assign user to organization of type: " + organizationType);
        }
        
        response.put("message", "User successfully assigned to organization");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/assignments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrganizationAssignments(@PathVariable Long id) {
        OrganizationDetailDTO orgDetail = organizationDetailService.getOrganizationDetailById(id);
        String organizationType = orgDetail.getOrganizationType();
        
        Map<String, Object> response = new HashMap<>();
        
        if ("Gift".equalsIgnoreCase(organizationType)) {
            response.put("assignments", giftAssignmentService.getGiftAssignmentsByOrganizationDetail(id));
            response.put("type", "Gift");
        } else if ("Academic Unit".equalsIgnoreCase(organizationType)) {
            response.put("assignments", academicUnitAssignmentService.getAcademicUnitAssignmentsByOrganizationDetail(id));
            response.put("type", "AcademicUnit");
        } else if ("Company".equalsIgnoreCase(organizationType)) {
            response.put("assignments", companyAssignmentService.getCompanyAssignmentsByOrganizationDetail(id));
            response.put("type", "Company");
        } else {
            response.put("assignments", java.util.Collections.emptyList());
            response.put("type", "Unknown");
        }
        
        return ResponseEntity.ok(response);
    }
}




