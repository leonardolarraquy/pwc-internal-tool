package com.pwc.repository;

import com.pwc.model.OrganizationDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationDetailRepository extends JpaRepository<OrganizationDetail, Long> {
    
    @Query("SELECT o FROM OrganizationDetail o WHERE " +
           "LOWER(o.legacyOrganizationName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<OrganizationDetail> searchOrganizationDetails(String search, Pageable pageable);
    
    @Query("SELECT o FROM OrganizationDetail o WHERE " +
           "(LOWER(o.legacyOrganizationName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.organization) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.organizationType) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(o.referenceId) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:organizationTypeFilter = 'all' OR LOWER(o.organizationType) = LOWER(:organizationTypeFilter))")
    Page<OrganizationDetail> searchOrganizationDetailsWithTypeFilter(String search, String organizationTypeFilter, Pageable pageable);
    
    @Query("SELECT o FROM OrganizationDetail o WHERE " +
           "(:organizationTypeFilter = 'all' OR LOWER(o.organizationType) = LOWER(:organizationTypeFilter))")
    Page<OrganizationDetail> findAllWithTypeFilter(String organizationTypeFilter, Pageable pageable);
    
    @Query("SELECT DISTINCT o.organizationType FROM OrganizationDetail o WHERE o.organizationType IS NOT NULL ORDER BY o.organizationType")
    List<String> findDistinctOrganizationTypes();
}




