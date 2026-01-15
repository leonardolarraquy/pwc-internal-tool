package com.pwc.repository;

import com.pwc.model.UserOrganizationAccess;
import com.pwc.model.User;
import com.pwc.model.OrganizationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOrganizationAccessRepository extends JpaRepository<UserOrganizationAccess, Long> {
    
    List<UserOrganizationAccess> findByUser(User user);
    
    List<UserOrganizationAccess> findByUserId(Long userId);
    
    Optional<UserOrganizationAccess> findByUserAndOrganizationType(User user, OrganizationType organizationType);
    
    Optional<UserOrganizationAccess> findByUserIdAndOrganizationTypeId(Long userId, Long organizationTypeId);
    
    void deleteByUser(User user);
    
    void deleteByUserId(Long userId);
    
    @Query("SELECT uoa FROM UserOrganizationAccess uoa WHERE uoa.user.id = :userId AND uoa.hasAccess = true")
    List<UserOrganizationAccess> findActiveAccessByUserId(@Param("userId") Long userId);
    
    @Query("SELECT uoa.user FROM UserOrganizationAccess uoa WHERE uoa.organizationType.id = :orgTypeId AND uoa.hasAccess = true")
    List<User> findUsersWithAccessToOrganizationType(@Param("orgTypeId") Long orgTypeId);
    
    @Query("SELECT COUNT(uoa) FROM UserOrganizationAccess uoa WHERE uoa.user.id = :userId AND uoa.hasAccess = true")
    Long countActiveAccessByUserId(@Param("userId") Long userId);
}
