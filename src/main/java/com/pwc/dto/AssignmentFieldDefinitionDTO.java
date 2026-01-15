package com.pwc.dto;

public class AssignmentFieldDefinitionDTO {
    private Long id;
    private Long organizationTypeId;
    private String organizationTypeName;
    private String fieldKey;
    private String fieldTitle;
    private String fieldDescription;
    private Integer displayOrder;
    private Boolean active;
    
    public AssignmentFieldDefinitionDTO() {}
    
    public AssignmentFieldDefinitionDTO(Long id, Long organizationTypeId, String organizationTypeName, 
                                        String fieldKey, String fieldTitle, String fieldDescription, 
                                        Integer displayOrder, Boolean active) {
        this.id = id;
        this.organizationTypeId = organizationTypeId;
        this.organizationTypeName = organizationTypeName;
        this.fieldKey = fieldKey;
        this.fieldTitle = fieldTitle;
        this.fieldDescription = fieldDescription;
        this.displayOrder = displayOrder;
        this.active = active;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getOrganizationTypeId() { return organizationTypeId; }
    public void setOrganizationTypeId(Long organizationTypeId) { this.organizationTypeId = organizationTypeId; }
    
    public String getOrganizationTypeName() { return organizationTypeName; }
    public void setOrganizationTypeName(String organizationTypeName) { this.organizationTypeName = organizationTypeName; }
    
    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
    
    public String getFieldTitle() { return fieldTitle; }
    public void setFieldTitle(String fieldTitle) { this.fieldTitle = fieldTitle; }
    
    public String getFieldDescription() { return fieldDescription; }
    public void setFieldDescription(String fieldDescription) { this.fieldDescription = fieldDescription; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
