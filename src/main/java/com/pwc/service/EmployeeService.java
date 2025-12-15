package com.pwc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pwc.dto.EmployeeCreateDTO;
import com.pwc.dto.EmployeeDTO;
import com.pwc.dto.EmployeeUpdateDTO;
import com.pwc.dto.PageResponse;
import com.pwc.model.Employee;
import com.pwc.repository.EmployeeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class EmployeeService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    
    private final EmployeeRepository employeeRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }
    
    public PageResponse<EmployeeDTO> getAllEmployees(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Employee> employeePage;
        if (search != null && !search.trim().isEmpty()) {
            employeePage = employeeRepository.searchEmployees(search.trim(), pageable);
        } else {
            employeePage = employeeRepository.findAll(pageable);
        }
        
        List<EmployeeDTO> content = employeePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                employeePage.getNumber(),
                employeePage.getSize(),
                employeePage.getTotalElements(),
                employeePage.getTotalPages(),
                employeePage.isLast()
        );
    }
    
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        return convertToDTO(employee);
    }
    
    public List<EmployeeDTO> searchEmployee(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new RuntimeException("Search query is required");
        }
        
        List<Employee> employees = employeeRepository.findByEmployeeIdOrEmailOrPositionId(query.trim());
        
        if (employees.isEmpty()) {
            throw new RuntimeException("Employee not found with the provided Worker ID, Email ID, or Position ID");
        }
        
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EmployeeDTO> findByWorkerId(String workerId) {
        if (workerId == null || workerId.trim().isEmpty()) {
            throw new RuntimeException("Worker ID is required");
        }
        
        List<Employee> employees = employeeRepository.findAllByEmployeeId(workerId.trim());
        
        if (employees.isEmpty()) {
            throw new RuntimeException("Employee not found with Worker ID: " + workerId);
        }
        
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EmployeeDTO> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        List<Employee> employees = employeeRepository.findAllByEmail(email.trim());
        
        if (employees.isEmpty()) {
            throw new RuntimeException("Employee not found with Email: " + email);
        }
        
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EmployeeDTO> findByPositionId(String positionId) {
        if (positionId == null || positionId.trim().isEmpty()) {
            throw new RuntimeException("Position ID is required");
        }
        
        List<Employee> employees = employeeRepository.findAllByPositionId(positionId.trim());
        
        if (employees.isEmpty()) {
            throw new RuntimeException("Employee not found with Position ID: " + positionId);
        }
        
        return employees.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public EmployeeDTO createEmployee(EmployeeCreateDTO employeeCreateDTO) {
        // Allow duplicate employeeIds (same employee can have different roles/positions)
        Employee employee = new Employee();
        employee.setEmployeeId(employeeCreateDTO.getEmployeeId());
        employee.setFirstName(employeeCreateDTO.getFirstName());
        employee.setLastName(employeeCreateDTO.getLastName());
        employee.setPositionId(employeeCreateDTO.getPositionId());
        employee.setPositionTitle(employeeCreateDTO.getPositionTitle());
        employee.setEmail(employeeCreateDTO.getEmail());
        
        Employee savedEmployee = employeeRepository.save(employee);
        return convertToDTO(savedEmployee);
    }
    
    @Transactional
    public EmployeeDTO updateEmployee(Long id, EmployeeUpdateDTO employeeUpdateDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        // Allow duplicate employeeIds (same employee can have different roles/positions)
        if (employeeUpdateDTO.getEmail() != null) {
            employee.setEmail(employeeUpdateDTO.getEmail());
        }
        
        if (employeeUpdateDTO.getEmployeeId() != null) {
            employee.setEmployeeId(employeeUpdateDTO.getEmployeeId());
        }
        
        if (employeeUpdateDTO.getFirstName() != null) {
            employee.setFirstName(employeeUpdateDTO.getFirstName());
        }
        if (employeeUpdateDTO.getLastName() != null) {
            employee.setLastName(employeeUpdateDTO.getLastName());
        }
        if (employeeUpdateDTO.getPositionId() != null) {
            employee.setPositionId(employeeUpdateDTO.getPositionId());
        }
        if (employeeUpdateDTO.getPositionTitle() != null) {
            employee.setPositionTitle(employeeUpdateDTO.getPositionTitle());
        }
        
        Employee updatedEmployee = employeeRepository.save(employee);
        return convertToDTO(updatedEmployee);
    }
    
    @Transactional
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found");
        }
        employeeRepository.deleteById(id);
    }
    
    public int importEmployeesFromCsv(MultipartFile file) {
        int importedCount = 0;
        int skippedCount = 0;
        int totalRecordsProcessed = 0;
        int emptyFieldsCount = 0;
        int saveErrorCount = 0;
        int exceptionCount = 0;
        
        logger.info("Starting CSV import process for file: {}", file.getOriginalFilename());
        
        try {
            // Read file content into memory first
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);
            
            // Detect delimiter (comma or pipe)
            char delimiter = detectDelimiter(fileContent);
            logger.info("Detected delimiter: '{}'", delimiter);
            
            // Parse CSV with detected delimiter
            try (BufferedReader reader = new BufferedReader(new StringReader(fileContent));
                 CSVParser csvParser = CSVFormat.DEFAULT
                         .builder()
                         .setDelimiter(delimiter)
                         .setHeader()
                         .setSkipHeaderRecord(true)
                         .setIgnoreHeaderCase(true)
                         .setTrim(true)
                         .build()
                         .parse(reader)) {
            
                // Get header map
                var headerMap = csvParser.getHeaderMap();
                if (headerMap == null || headerMap.isEmpty()) {
                    throw new RuntimeException("CSV file is empty or has no headers");
                }
                
                logger.info("CSV headers found: {}", headerMap.keySet());
                
                // Find column names (case-insensitive)
                String emailColumn = null, employeeIdColumn = null, firstNameColumn = null, lastNameColumn = null;
                String positionIdColumn = null, positionTitleColumn = null;
                
                for (String header : headerMap.keySet()) {
                    String headerLower = header.toLowerCase().trim();
                    if (headerLower.contains("email") && emailColumn == null) emailColumn = header;
                    else if (headerLower.contains("employee") && headerLower.contains("id") && employeeIdColumn == null) 
                        employeeIdColumn = header;
                    else if (headerLower.contains("first") && headerLower.contains("name") && firstNameColumn == null) 
                        firstNameColumn = header;
                    else if (headerLower.contains("last") && headerLower.contains("name") && lastNameColumn == null) 
                        lastNameColumn = header;
                    else if (headerLower.contains("position") && headerLower.contains("id") && positionIdColumn == null) 
                        positionIdColumn = header;
                    else if (headerLower.contains("position") && headerLower.contains("title") && positionTitleColumn == null) 
                        positionTitleColumn = header;
                }
                
                logger.info("Mapped columns - email: {}, employeeId: {}, firstName: {}, lastName: {}", 
                    emailColumn, employeeIdColumn, firstNameColumn, lastNameColumn);
                
                // Only employeeId is required, other fields are optional
                if (employeeIdColumn == null) {
                    throw new RuntimeException("CSV must contain: employeeId column");
                }
                
                // Process each record
                for (CSVRecord record : csvParser) {
                    totalRecordsProcessed++;
                    long recordNumber = record.getRecordNumber();
                    
                    try {
                        // Get employeeId (required)
                        String employeeId = record.get(employeeIdColumn).trim();
                        
                        // Check if employeeId is empty (only required field)
                        if (employeeId.isEmpty()) {
                            skippedCount++;
                            emptyFieldsCount++;
                            logger.warn("SKIP [Line {}]: EmployeeID is required and cannot be empty", recordNumber);
                            continue;
                        }
                        
                        // Get optional fields (can be empty)
                        String email = null;
                        String firstName = null;
                        String lastName = null;
                        
                        if (emailColumn != null && record.isMapped(emailColumn)) {
                            String emailValue = record.get(emailColumn).trim();
                            if (!emailValue.isEmpty()) {
                                email = emailValue;
                            }
                        }
                        
                        if (firstNameColumn != null && record.isMapped(firstNameColumn)) {
                            String firstNameValue = record.get(firstNameColumn).trim();
                            if (!firstNameValue.isEmpty()) {
                                firstName = firstNameValue;
                            }
                        }
                        
                        if (lastNameColumn != null && record.isMapped(lastNameColumn)) {
                            String lastNameValue = record.get(lastNameColumn).trim();
                            if (!lastNameValue.isEmpty()) {
                                lastName = lastNameValue;
                            }
                        }
                        
                        // Get optional fields
                        String positionId = null;
                        String positionTitle = null;
                        
                        if (positionIdColumn != null && record.isMapped(positionIdColumn)) {
                            String positionIdValue = record.get(positionIdColumn).trim();
                            if (!positionIdValue.isEmpty()) {
                                positionId = positionIdValue;
                            }
                        }
                        if (positionTitleColumn != null && record.isMapped(positionTitleColumn)) {
                            String positionTitleValue = record.get(positionTitleColumn).trim();
                            if (!positionTitleValue.isEmpty()) {
                                positionTitle = positionTitleValue;
                            }
                        }
                        
                        // Allow duplicate employeeIds (same employee can have different roles/positions)
                        Employee employee = new Employee();
                        employee.setEmployeeId(employeeId);
                        
                        if (email != null) {
                            employee.setEmail(email);
                        }
                        if (firstName != null) {
                            employee.setFirstName(firstName);
                        }
                        if (lastName != null) {
                            employee.setLastName(lastName);
                        }
                        if (positionId != null) {
                            employee.setPositionId(positionId);
                        }
                        if (positionTitle != null) {
                            employee.setPositionTitle(positionTitle);
                        }
                        
                        // Save employee in separate transaction to avoid rollback affecting other records
                        if (saveEmployeeInNewTransaction(employee)) {
                            importedCount++;
                            if (importedCount % 100 == 0) {
                                logger.debug("Progress: {} employees imported so far", importedCount);
                            }
                        } else {
                            skippedCount++;
                            saveErrorCount++;
                            logger.error("SKIP [Line {}]: Failed to save employee. EmployeeID: '{}'", 
                                recordNumber, employeeId);
                        }
                        
                    } catch (Exception e) {
                        skippedCount++;
                        exceptionCount++;
                        logger.error("SKIP [Line {}]: Exception while processing record - {}", recordNumber, e.getMessage(), e);
                        // Continue processing other rows
                    }
                }
            }
            
            // Log summary
            logger.info("=== CSV Import Summary ===");
            logger.info("Total records processed: {}", totalRecordsProcessed);
            logger.info("Successfully imported: {}", importedCount);
            logger.info("Skipped: {}", skippedCount);
            logger.info("  - Empty required fields: {}", emptyFieldsCount);
            logger.info("  - Save errors: {}", saveErrorCount);
            logger.info("  - Exceptions: {}", exceptionCount);
            logger.info("===========================");
            
        } catch (IOException e) {
            logger.error("Error reading CSV file: {}", e.getMessage(), e);
            throw new RuntimeException("Error reading CSV file: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Error importing CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Error importing CSV: " + e.getMessage(), e);
        }
        
        return importedCount;
    }
    
    /**
     * Detects the delimiter used in the CSV file (comma or pipe)
     * by analyzing the first line of the file content
     */
    private char detectDelimiter(String fileContent) {
        if (fileContent == null || fileContent.isEmpty()) {
            return ','; // Default to comma
        }
        
        // Read first line to detect delimiter
        String firstLine = fileContent.split("\n", 2)[0];
        if (firstLine == null || firstLine.isEmpty()) {
            return ','; // Default to comma
        }
        
        // Count occurrences of comma and pipe
        long commaCount = firstLine.chars().filter(ch -> ch == ',').count();
        long pipeCount = firstLine.chars().filter(ch -> ch == '|').count();
        
        // Return the delimiter with more occurrences
        // If equal or both zero, default to comma
        return pipeCount > commaCount ? '|' : ',';
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean saveEmployeeInNewTransaction(Employee employee) {
        try {
            employeeRepository.save(employee);
            return true;
        } catch (Exception e) {
            // Log error but don't throw - return false to indicate failure
            logger.error("Error saving employee in transaction - Email: '{}', EmployeeID: '{}', Error: {}", 
                employee.getEmail(), employee.getEmployeeId(), e.getMessage(), e);
            return false;
        }
    }
    
    private EmployeeDTO convertToDTO(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getEmployeeId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getPositionId(),
                employee.getPositionTitle(),
                employee.getEmail()
        );
    }
}

