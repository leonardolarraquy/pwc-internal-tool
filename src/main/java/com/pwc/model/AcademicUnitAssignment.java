package com.pwc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "academic_unit_assignments")
public class AcademicUnitAssignment extends BaseAssignment {
    
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
        super();
    }
    
    public AcademicUnitAssignment(Employee employee, OrganizationDetail organizationDetail, User createdBy) {
        super();
        setEmployee(employee);
        setOrganizationDetail(organizationDetail);
        setCreatedBy(createdBy);
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






