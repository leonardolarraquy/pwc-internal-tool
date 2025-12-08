package com.pwc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "academic_unit_assignments")
public class AcademicUnitAssignment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_detail_id", nullable = false)
    private OrganizationDetail organizationDetail;
    
    @Column(name = "hcm_academic_chair_au")
    private Boolean hcmAcademicChairAu = false;
    
    @Column(name = "hcm_academic_dean_auh")
    private Boolean hcmAcademicDeanAuh = false;
    
    @Column(name = "hcm_academic_faculty_executive_auh")
    private Boolean hcmAcademicFacultyExecutiveAuh = false;
    
    @Column(name = "hcm_academic_faculty_hr_analyst_au")
    private Boolean hcmAcademicFacultyHrAnalystAu = false;
    
    @Column(name = "hcm_academic_provost_partner_auh")
    private Boolean hcmAcademicProvostPartnerAuh = false;
    
    @Column(name = "hcm_academic_school_director_auh")
    private Boolean hcmAcademicSchoolDirectorAuh = false;
    
    public AcademicUnitAssignment() {
    }
    
    public AcademicUnitAssignment(User user, OrganizationDetail organizationDetail) {
        this.user = user;
        this.organizationDetail = organizationDetail;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public OrganizationDetail getOrganizationDetail() {
        return organizationDetail;
    }
    
    public void setOrganizationDetail(OrganizationDetail organizationDetail) {
        this.organizationDetail = organizationDetail;
    }
    
    public Boolean getHcmAcademicChairAu() {
        return hcmAcademicChairAu != null ? hcmAcademicChairAu : false;
    }
    
    public void setHcmAcademicChairAu(Boolean hcmAcademicChairAu) {
        this.hcmAcademicChairAu = hcmAcademicChairAu;
    }
    
    public Boolean getHcmAcademicDeanAuh() {
        return hcmAcademicDeanAuh != null ? hcmAcademicDeanAuh : false;
    }
    
    public void setHcmAcademicDeanAuh(Boolean hcmAcademicDeanAuh) {
        this.hcmAcademicDeanAuh = hcmAcademicDeanAuh;
    }
    
    public Boolean getHcmAcademicFacultyExecutiveAuh() {
        return hcmAcademicFacultyExecutiveAuh != null ? hcmAcademicFacultyExecutiveAuh : false;
    }
    
    public void setHcmAcademicFacultyExecutiveAuh(Boolean hcmAcademicFacultyExecutiveAuh) {
        this.hcmAcademicFacultyExecutiveAuh = hcmAcademicFacultyExecutiveAuh;
    }
    
    public Boolean getHcmAcademicFacultyHrAnalystAu() {
        return hcmAcademicFacultyHrAnalystAu != null ? hcmAcademicFacultyHrAnalystAu : false;
    }
    
    public void setHcmAcademicFacultyHrAnalystAu(Boolean hcmAcademicFacultyHrAnalystAu) {
        this.hcmAcademicFacultyHrAnalystAu = hcmAcademicFacultyHrAnalystAu;
    }
    
    public Boolean getHcmAcademicProvostPartnerAuh() {
        return hcmAcademicProvostPartnerAuh != null ? hcmAcademicProvostPartnerAuh : false;
    }
    
    public void setHcmAcademicProvostPartnerAuh(Boolean hcmAcademicProvostPartnerAuh) {
        this.hcmAcademicProvostPartnerAuh = hcmAcademicProvostPartnerAuh;
    }
    
    public Boolean getHcmAcademicSchoolDirectorAuh() {
        return hcmAcademicSchoolDirectorAuh != null ? hcmAcademicSchoolDirectorAuh : false;
    }
    
    public void setHcmAcademicSchoolDirectorAuh(Boolean hcmAcademicSchoolDirectorAuh) {
        this.hcmAcademicSchoolDirectorAuh = hcmAcademicSchoolDirectorAuh;
    }
}



