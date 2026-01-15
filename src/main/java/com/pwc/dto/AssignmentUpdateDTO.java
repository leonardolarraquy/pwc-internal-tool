package com.pwc.dto;

import java.util.Map;

public class AssignmentUpdateDTO {
    private Map<String, Boolean> fieldValues;  // fieldKey -> value
    
    public AssignmentUpdateDTO() {}
    
    // Getters and Setters
    public Map<String, Boolean> getFieldValues() { return fieldValues; }
    public void setFieldValues(Map<String, Boolean> fieldValues) { this.fieldValues = fieldValues; }
}
