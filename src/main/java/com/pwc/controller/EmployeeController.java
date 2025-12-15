package com.pwc.controller;

import com.pwc.dto.*;
import com.pwc.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    
    private final EmployeeService employeeService;
    
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<EmployeeDTO>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        PageResponse<EmployeeDTO> response = employeeService.getAllEmployees(page, size, sortBy, sortDir, search);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeCreateDTO employeeCreateDTO) {
        EmployeeDTO employee = employeeService.createEmployee(employeeCreateDTO);
        return ResponseEntity.ok(employee);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id, 
                                                      @Valid @RequestBody EmployeeUpdateDTO employeeUpdateDTO) {
        EmployeeDTO employee = employeeService.updateEmployee(id, employeeUpdateDTO);
        return ResponseEntity.ok(employee);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importEmployees(@RequestParam("file") MultipartFile file) {
        int importedCount = employeeService.importEmployeesFromCsv(file);
        Map<String, Object> response = new HashMap<>();
        response.put("imported", importedCount);
        response.put("message", "Successfully imported " + importedCount + " employees");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmployeeDTO>> searchEmployee(@RequestParam String query) {
        List<EmployeeDTO> employees = employeeService.searchEmployee(query);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/find-by-worker-id")
    public ResponseEntity<List<EmployeeDTO>> findByWorkerId(@RequestParam String workerId) {
        List<EmployeeDTO> employees = employeeService.findByWorkerId(workerId);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/find-by-email")
    public ResponseEntity<List<EmployeeDTO>> findByEmail(@RequestParam String email) {
        List<EmployeeDTO> employees = employeeService.findByEmail(email);
        return ResponseEntity.ok(employees);
    }
    
    @GetMapping("/find-by-position-id")
    public ResponseEntity<List<EmployeeDTO>> findByPositionId(@RequestParam String positionId) {
        List<EmployeeDTO> employees = employeeService.findByPositionId(positionId);
        return ResponseEntity.ok(employees);
    }
}

