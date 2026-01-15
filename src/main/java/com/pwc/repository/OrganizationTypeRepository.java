package com.pwc.repository;

import com.pwc.model.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationTypeRepository extends JpaRepository<OrganizationType, Long> {
    
    Optional<OrganizationType> findBySlug(String slug);
    
    Optional<OrganizationType> findByName(String name);
    
    @Query("SELECT ot FROM OrganizationType ot WHERE ot.active = true ORDER BY ot.displayOrder ASC")
    List<OrganizationType> findAllActive();
    
    @Query("SELECT ot FROM OrganizationType ot ORDER BY ot.displayOrder ASC")
    List<OrganizationType> findAllOrderByDisplayOrder();
    
    boolean existsByName(String name);
    
    boolean existsBySlug(String slug);
}
