package com.pwc.dto;

public class AssignmentFieldDefinitionCreateDTO {
    private Long organizationTypeId;
    private String fieldKey;
    private String fieldTitle;
    private String fieldDescription;
    private Integer displayOrder;
    
    public AssignmentFieldDefinitionCreateDTO() {}
    
    // Getters and Setters
    public Long getOrganizationTypeId() { return organizationTypeId; }
    public void setOrganizationTypeId(Long organizationTypeId) { this.organizationTypeId = organizationTypeId; }
    
    public String getFieldKey() { return fieldKey; }
    public void setFieldKey(String fieldKey) { this.fieldKey = fieldKey; }
    
    public String getFieldTitle() { return fieldTitle; }
    public void setFieldTitle(String fieldTitle) { this.fieldTitle = fieldTitle; }
    
    public String getFieldDescription() { return fieldDescription; }
    public void setFieldDescription(String fieldDescription) { this.fieldDescription = fieldDescription; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
}
