package com.pwc.dto;

import java.util.List;

public class OrganizationTypeDTO {
    private Long id;
    private String name;
    private String slug;
    private String displayName;
    private String iconName;
    private Integer displayOrder;
    private Boolean active;
    private List<AssignmentFieldDefinitionDTO> fieldDefinitions;
    
    public OrganizationTypeDTO() {}
    
    public OrganizationTypeDTO(Long id, String name, String slug, String displayName, String iconName, Integer displayOrder, Boolean active) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.displayName = displayName;
        this.iconName = iconName;
        this.displayOrder = displayOrder;
        this.active = active;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getIconName() { return iconName; }
    public void setIconName(String iconName) { this.iconName = iconName; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    
    public List<AssignmentFieldDefinitionDTO> getFieldDefinitions() { return fieldDefinitions; }
    public void setFieldDefinitions(List<AssignmentFieldDefinitionDTO> fieldDefinitions) { this.fieldDefinitions = fieldDefinitions; }
}
