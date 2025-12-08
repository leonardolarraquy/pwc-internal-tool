package com.pwc.service;

import com.pwc.dto.*;
import com.pwc.model.OrganizationDetail;
import com.pwc.repository.OrganizationDetailRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Service
public class OrganizationDetailService {
    
    private final OrganizationDetailRepository organizationDetailRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public OrganizationDetailService(OrganizationDetailRepository organizationDetailRepository) {
        this.organizationDetailRepository = organizationDetailRepository;
    }
    
    public PageResponse<OrganizationDetailDTO> getAllOrganizationDetails(int page, int size, String sortBy, String sortDir, String search, String organizationTypeFilter) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Default organizationTypeFilter to 'all' if null
        if (organizationTypeFilter == null || organizationTypeFilter.trim().isEmpty()) {
            organizationTypeFilter = "all";
        }
        
        Page<OrganizationDetail> organizationDetailPage;
        if (search != null && !search.trim().isEmpty()) {
            organizationDetailPage = organizationDetailRepository.searchOrganizationDetailsWithTypeFilter(search.trim(), organizationTypeFilter, pageable);
        } else {
            if ("all".equals(organizationTypeFilter)) {
                organizationDetailPage = organizationDetailRepository.findAll(pageable);
            } else {
                organizationDetailPage = organizationDetailRepository.findAllWithTypeFilter(organizationTypeFilter, pageable);
            }
        }
        
        List<OrganizationDetailDTO> content = organizationDetailPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                organizationDetailPage.getNumber(),
                organizationDetailPage.getSize(),
                organizationDetailPage.getTotalElements(),
                organizationDetailPage.getTotalPages(),
                organizationDetailPage.isLast()
        );
    }
    
    public List<String> getDistinctOrganizationTypes() {
        return organizationDetailRepository.findDistinctOrganizationTypes();
    }
    
    public OrganizationDetailDTO getOrganizationDetailById(Long id) {
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        return convertToDTO(organizationDetail);
    }
    
    @Transactional
    public OrganizationDetailDTO createOrganizationDetail(OrganizationDetailCreateDTO createDTO) {
        OrganizationDetail organizationDetail = new OrganizationDetail();
        organizationDetail.setLegacyOrganizationName(createDTO.getLegacyOrganizationName());
        organizationDetail.setOrganization(createDTO.getOrganization());
        organizationDetail.setOrganizationType(createDTO.getOrganizationType());
        organizationDetail.setReferenceId(createDTO.getReferenceId());
        
        OrganizationDetail saved = organizationDetailRepository.save(organizationDetail);
        return convertToDTO(saved);
    }
    
    @Transactional
    public OrganizationDetailDTO updateOrganizationDetail(Long id, OrganizationDetailUpdateDTO updateDTO) {
        OrganizationDetail organizationDetail = organizationDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization detail not found"));
        
        if (updateDTO.getLegacyOrganizationName() != null) {
            organizationDetail.setLegacyOrganizationName(updateDTO.getLegacyOrganizationName());
        }
        if (updateDTO.getOrganization() != null) {
            organizationDetail.setOrganization(updateDTO.getOrganization());
        }
        if (updateDTO.getOrganizationType() != null) {
            organizationDetail.setOrganizationType(updateDTO.getOrganizationType());
        }
        if (updateDTO.getReferenceId() != null) {
            organizationDetail.setReferenceId(updateDTO.getReferenceId());
        }
        
        OrganizationDetail updated = organizationDetailRepository.save(organizationDetail);
        return convertToDTO(updated);
    }
    
    @Transactional
    public void deleteOrganizationDetail(Long id) {
        if (!organizationDetailRepository.existsById(id)) {
            throw new RuntimeException("Organization detail not found");
        }
        organizationDetailRepository.deleteById(id);
    }
    
    public int importOrganizationDetailsFromCsv(MultipartFile file) {
        int importedCount = 0;
        @SuppressWarnings("unused")
        int skippedCount = 0;
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = CSVFormat.DEFAULT
                     .builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .setIgnoreHeaderCase(true)
                     .setTrim(true)
                     .build()
                     .parse(reader)) {
            
            // Get header map
            var headerMap = csvParser.getHeaderMap();
            if (headerMap == null || headerMap.isEmpty()) {
                throw new RuntimeException("CSV file is empty or has no headers");
            }
            
            // Find column names (case-insensitive)
            String legacyOrganizationNameColumn = null;
            String organizationColumn = null;
            String organizationTypeColumn = null;
            String referenceIdColumn = null;
            
            for (String header : headerMap.keySet()) {
                String headerLower = header.toLowerCase().trim();
                if (headerLower.contains("legacy") && headerLower.contains("organization") && headerLower.contains("name") && legacyOrganizationNameColumn == null) {
                    legacyOrganizationNameColumn = header;
                } else if (headerLower.equals("organization") || (headerLower.contains("organization") && !headerLower.contains("legacy") && !headerLower.contains("type") && organizationColumn == null)) {
                    organizationColumn = header;
                } else if (headerLower.contains("organization") && headerLower.contains("type") && organizationTypeColumn == null) {
                    organizationTypeColumn = header;
                } else if ((headerLower.contains("reference") && headerLower.contains("id")) || headerLower.equals("referenceid") && referenceIdColumn == null) {
                    referenceIdColumn = header;
                }
            }
            
            // Process each record - all fields are optional
            for (CSVRecord record : csvParser) {
                try {
                    OrganizationDetail organizationDetail = new OrganizationDetail();
                    
                    if (legacyOrganizationNameColumn != null && record.isMapped(legacyOrganizationNameColumn)) {
                        String value = record.get(legacyOrganizationNameColumn).trim();
                        if (!value.isEmpty() && value.length() <= 100) {
                            organizationDetail.setLegacyOrganizationName(value);
                        }
                    }
                    
                    if (organizationColumn != null && record.isMapped(organizationColumn)) {
                        String value = record.get(organizationColumn).trim();
                        if (!value.isEmpty() && value.length() <= 100) {
                            organizationDetail.setOrganization(value);
                        }
                    }
                    
                    if (organizationTypeColumn != null && record.isMapped(organizationTypeColumn)) {
                        String value = record.get(organizationTypeColumn).trim();
                        if (!value.isEmpty() && value.length() <= 100) {
                            organizationDetail.setOrganizationType(value);
                        }
                    }
                    
                    if (referenceIdColumn != null && record.isMapped(referenceIdColumn)) {
                        String value = record.get(referenceIdColumn).trim();
                        // Clean up Excel error values like "#VALUE!", "#REF!", "#N/A", etc.
                        // Only set referenceId if it's valid (not empty, not an Excel error, and within length limit)
                        if (!value.isEmpty() && !value.startsWith("#") && value.length() <= 100) {
                            organizationDetail.setReferenceId(value);
                        }
                        // If value is an Excel error or empty, referenceId will remain null (default)
                    }
                    
                    // Save in separate transaction to avoid rollback affecting other records
                    if (saveOrganizationDetailInNewTransaction(organizationDetail)) {
                        importedCount++;
                    } else {
                        skippedCount++;
                    }
                    
                } catch (Exception e) {
                    skippedCount++;
                    // Continue processing other rows
                }
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Error importing CSV: " + e.getMessage(), e);
        }
        
        return importedCount;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean saveOrganizationDetailInNewTransaction(OrganizationDetail organizationDetail) {
        try {
            organizationDetailRepository.save(organizationDetail);
            return true;
        } catch (Exception e) {
            // Log error but don't throw - return false to indicate failure
            return false;
        }
    }
    
    private OrganizationDetailDTO convertToDTO(OrganizationDetail organizationDetail) {
        String referenceId = organizationDetail.getReferenceId();
        // Clean up Excel error values like "#VALUE!", "#REF!", "#N/A", etc.
        if (referenceId != null && (referenceId.startsWith("#") || referenceId.trim().isEmpty())) {
            referenceId = null;
        }
        
        return new OrganizationDetailDTO(
                organizationDetail.getId(),
                organizationDetail.getLegacyOrganizationName(),
                organizationDetail.getOrganization(),
                organizationDetail.getOrganizationType(),
                referenceId
        );
    }
}




