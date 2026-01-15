package com.pwc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "assignment_field_values", indexes = {
    @Index(name = "idx_field_values_assignment", columnList = "assignment_id"),
    @Index(name = "idx_field_values_field_def", columnList = "field_definition_id"),
    @Index(name = "idx_field_values_composite", columnList = "assignment_id, field_definition_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_field_value_assignment_field", columnNames = {"assignment_id", "field_definition_id"})
})
public class AssignmentFieldValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_definition_id", nullable = false)
    private AssignmentFieldDefinition fieldDefinition;
    
    @Column(name = "field_value")
    private Boolean value = false;
    
    // Constructors
    public AssignmentFieldValue() {}
    
    public AssignmentFieldValue(Assignment assignment, AssignmentFieldDefinition fieldDefinition, Boolean value) {
        this.assignment = assignment;
        this.fieldDefinition = fieldDefinition;
        this.value = value;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    
    public AssignmentFieldDefinition getFieldDefinition() { return fieldDefinition; }
    public void setFieldDefinition(AssignmentFieldDefinition fieldDefinition) { this.fieldDefinition = fieldDefinition; }
    
    public Boolean getValue() { return value != null ? value : false; }
    public void setValue(Boolean value) { this.value = value; }
}
