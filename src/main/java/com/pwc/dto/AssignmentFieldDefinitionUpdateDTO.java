package com.pwc.dto;

public class AssignmentFieldDefinitionUpdateDTO {
    private String fieldKey;
    private String fieldTitle;
    private String fieldDescription;
    private Integer displayOrder;
    private Boolean active;
    
    public AssignmentFieldDefinitionUpdateDTO() {}
    
    // Getters and Setters
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
