package com.pwc.dto;

public class AcademicUnitAssignmentDTO {
    private Long id;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userEmployeeId;
    private Long organizationDetailId;
    private String organizationName;
    private String organizationType;
    private String referenceId;
    private Boolean hcmAcademicChairAu;
    private Boolean hcmAcademicDeanAuh;
    private Boolean hcmAcademicFacultyExecutiveAuh;
    private Boolean hcmAcademicFacultyHrAnalystAu;
    private Boolean hcmAcademicProvostPartnerAuh;
    private Boolean hcmAcademicSchoolDirectorAuh;
    
    public AcademicUnitAssignmentDTO() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getUserFirstName() {
        return userFirstName;
    }
    
    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }
    
    public String getUserLastName() {
        return userLastName;
    }
    
    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserEmployeeId() {
        return userEmployeeId;
    }
    
    public void setUserEmployeeId(String userEmployeeId) {
        this.userEmployeeId = userEmployeeId;
    }
    
    public Long getOrganizationDetailId() {
        return organizationDetailId;
    }
    
    public void setOrganizationDetailId(Long organizationDetailId) {
        this.organizationDetailId = organizationDetailId;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }
    
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    
    public String getOrganizationType() {
        return organizationType;
    }
    
    public void setOrganizationType(String organizationType) {
        this.organizationType = organizationType;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
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



