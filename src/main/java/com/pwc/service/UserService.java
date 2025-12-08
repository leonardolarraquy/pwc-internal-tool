package com.pwc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.pwc.dto.LoginResponse;
import com.pwc.dto.PageResponse;
import com.pwc.dto.UserCreateDTO;
import com.pwc.dto.UserDTO;
import com.pwc.dto.UserUpdateDTO;
import com.pwc.model.Role;
import com.pwc.model.User;
import com.pwc.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public LoginResponse authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Si el password es NULL, permitir login con cualquier password (solo para forzar cambio)
        boolean mustChangePassword = false;
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            mustChangePassword = true;
            // No validamos el password si es NULL, pero aún requerimos que se envíe algo
            if (password == null || password.isEmpty()) {
                throw new RuntimeException("Password required. Please set your password.");
            }
        } else {
            // Validar password normal
            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid email or password");
            }
        }
        
        LoginResponse response = new LoginResponse();
        response.setToken("token_placeholder"); // Will be set by controller
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setMustChangePassword(mustChangePassword);
        // Los getters ya manejan null como false
        response.setCompanyAssignmentsAccess(user.getCompanyAssignmentsAccess());
        response.setAcademicUnitAssignmentsAccess(user.getAcademicUnitAssignmentsAccess());
        response.setGiftAssignmentsAccess(user.getGiftAssignmentsAccess());
        
        return response;
    }
    
    @Transactional
    public void changePassword(String email, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }
        
        if (newPassword.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    @Transactional
    public void resetPassword(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Use native query to set password to NULL, bypassing Hibernate validation
        entityManager.createNativeQuery("UPDATE users SET password = NULL WHERE id = :id")
                .setParameter("id", userId)
                .executeUpdate();
        
        // Refresh the entity to reflect the change
        entityManager.refresh(user);
    }
    
    public PageResponse<UserDTO> getAllUsers(int page, int size, String sortBy, String sortDir, String search, String accessFilter) {
        Sort sort = sortDir.equalsIgnoreCase("desc") 
                ? Sort.by(sortBy).descending() 
                : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Default accessFilter to 'all' if null
        if (accessFilter == null || accessFilter.trim().isEmpty()) {
            accessFilter = "all";
        }
        
        Page<User> userPage;
        if (search != null && !search.trim().isEmpty()) {
            userPage = userRepository.searchUsersWithAccessFilter(search.trim(), accessFilter, pageable);
        } else {
            if ("all".equals(accessFilter)) {
                userPage = userRepository.findAll(pageable);
            } else {
                userPage = userRepository.findAllWithAccessFilter(accessFilter, pageable);
            }
        }
        
        List<UserDTO> content = userPage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return new PageResponse<>(
                content,
                userPage.getNumber(),
                userPage.getSize(),
                userPage.getTotalElements(),
                userPage.getTotalPages(),
                userPage.isLast()
        );
    }
    
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }
    
    public List<UserDTO> searchUser(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new RuntimeException("Search query is required");
        }
        
        List<User> users = userRepository.findByEmployeeIdOrEmailOrPositionId(query.trim());
        
        if (users.isEmpty()) {
            throw new RuntimeException("User not found with the provided Worker ID, Email ID, or Position ID");
        }
        
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        // Check if a complete duplicate exists (all fields must match)
        boolean isDuplicate = userRepository.existsByAllFields(
            userCreateDTO.getEmployeeId(),
            userCreateDTO.getFirstName(),
            userCreateDTO.getLastName(),
            userCreateDTO.getEmail(),
            userCreateDTO.getPositionId(),
            userCreateDTO.getPositionTitle()
        );
        
        if (isDuplicate) {
            throw new RuntimeException("A user with the same employee ID, email, name, and position already exists");
        }
        
        User user = new User();
        user.setEmployeeId(userCreateDTO.getEmployeeId());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPositionId(userCreateDTO.getPositionId());
        user.setPositionTitle(userCreateDTO.getPositionTitle());
        user.setEmail(userCreateDTO.getEmail());
        if (userCreateDTO.getPassword() != null && !userCreateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        } else {
            user.setPassword(null); // User must set password on first login
        }
        user.setRole(userCreateDTO.getRole() != null ? userCreateDTO.getRole() : Role.USER);
        // Los campos de acceso pueden ser null, se manejarán como false en los getters
        user.setCompanyAssignmentsAccess(userCreateDTO.getCompanyAssignmentsAccess());
        user.setAcademicUnitAssignmentsAccess(userCreateDTO.getAcademicUnitAssignmentsAccess());
        user.setGiftAssignmentsAccess(userCreateDTO.getGiftAssignmentsAccess());
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Collect final values after updates
        String finalEmail = userUpdateDTO.getEmail() != null ? userUpdateDTO.getEmail() : user.getEmail();
        String finalEmployeeId = userUpdateDTO.getEmployeeId() != null ? userUpdateDTO.getEmployeeId() : user.getEmployeeId();
        String finalFirstName = userUpdateDTO.getFirstName() != null ? userUpdateDTO.getFirstName() : user.getFirstName();
        String finalLastName = userUpdateDTO.getLastName() != null ? userUpdateDTO.getLastName() : user.getLastName();
        String finalPositionId = userUpdateDTO.getPositionId() != null ? userUpdateDTO.getPositionId() : user.getPositionId();
        String finalPositionTitle = userUpdateDTO.getPositionTitle() != null ? userUpdateDTO.getPositionTitle() : user.getPositionTitle();
        
        // Check if updating would create a complete duplicate with another user (excluding current user)
        boolean isDuplicate = userRepository.existsByAllFieldsExcludingId(
            user.getId(), finalEmployeeId, finalFirstName, finalLastName, finalEmail, finalPositionId, finalPositionTitle);
        
        if (isDuplicate) {
            throw new RuntimeException("A user with the same employee ID, email, name, and position already exists");
        }
        
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
        }
        
        if (userUpdateDTO.getEmployeeId() != null) {
            user.setEmployeeId(userUpdateDTO.getEmployeeId());
        }
        
        if (userUpdateDTO.getFirstName() != null) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            user.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getPositionId() != null) {
            user.setPositionId(userUpdateDTO.getPositionId());
        }
        if (userUpdateDTO.getPositionTitle() != null) {
            user.setPositionTitle(userUpdateDTO.getPositionTitle());
        }
        if (userUpdateDTO.getPassword() != null && !userUpdateDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }
        if (userUpdateDTO.getRole() != null) {
            user.setRole(userUpdateDTO.getRole());
        }
        if (userUpdateDTO.getCompanyAssignmentsAccess() != null) {
            user.setCompanyAssignmentsAccess(userUpdateDTO.getCompanyAssignmentsAccess());
        }
        if (userUpdateDTO.getAcademicUnitAssignmentsAccess() != null) {
            user.setAcademicUnitAssignmentsAccess(userUpdateDTO.getAcademicUnitAssignmentsAccess());
        }
        if (userUpdateDTO.getGiftAssignmentsAccess() != null) {
            user.setGiftAssignmentsAccess(userUpdateDTO.getGiftAssignmentsAccess());
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    
    public int importUsersFromCsv(MultipartFile file) {
        int importedCount = 0;
        int skippedCount = 0;
        int totalRecordsProcessed = 0;
        int emptyFieldsCount = 0;
        int duplicateRecordCount = 0;
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
                String positionIdColumn = null, positionTitleColumn = null, passwordColumn = null, roleColumn = null;
                
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
                    else if (headerLower.contains("password") && passwordColumn == null) passwordColumn = header;
                    else if (headerLower.contains("role") && roleColumn == null) roleColumn = header;
                }
                
                logger.info("Mapped columns - email: {}, employeeId: {}, firstName: {}, lastName: {}", 
                    emailColumn, employeeIdColumn, firstNameColumn, lastNameColumn);
                
                if (emailColumn == null || employeeIdColumn == null || firstNameColumn == null || lastNameColumn == null) {
                    throw new RuntimeException("CSV must contain: email, employeeId, firstName, lastName columns");
                }
                
                // Process each record
                for (CSVRecord record : csvParser) {
                    totalRecordsProcessed++;
                    long recordNumber = record.getRecordNumber();
                    
                    try {
                        String email = record.get(emailColumn).trim();
                        String employeeId = record.get(employeeIdColumn).trim();
                        String firstName = record.get(firstNameColumn).trim();
                        String lastName = record.get(lastNameColumn).trim();
                        
                        // Check for empty required fields
                        if (email.isEmpty() || employeeId.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                            skippedCount++;
                            emptyFieldsCount++;
                            StringBuilder missingFields = new StringBuilder();
                            if (email.isEmpty()) missingFields.append("email ");
                            if (employeeId.isEmpty()) missingFields.append("employeeId ");
                            if (firstName.isEmpty()) missingFields.append("firstName ");
                            if (lastName.isEmpty()) missingFields.append("lastName ");
                            logger.warn("SKIP [Line {}]: Empty required fields - missing: {}. Email: '{}', EmployeeID: '{}', FirstName: '{}', LastName: '{}'", 
                                recordNumber, missingFields.toString().trim(), email, employeeId, firstName, lastName);
                            continue;
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
                        
                        // Check if a complete duplicate exists (all fields must match)
                        boolean isDuplicate = userRepository.existsByAllFields(
                            employeeId, firstName, lastName, email, positionId, positionTitle);
                        
                        if (isDuplicate) {
                            skippedCount++;
                            duplicateRecordCount++;
                            logger.warn("SKIP [Line {}]: Complete duplicate record - EmployeeID: '{}', Email: '{}', FirstName: '{}', LastName: '{}', PositionID: '{}', PositionTitle: '{}'", 
                                recordNumber, employeeId, email, firstName, lastName, 
                                positionId != null ? positionId : "(empty)", 
                                positionTitle != null ? positionTitle : "(empty)");
                            continue;
                        }
                        
                        User user = new User();
                        user.setEmail(email);
                        user.setEmployeeId(employeeId);
                        user.setFirstName(firstName);
                        user.setLastName(lastName);
                        
                        if (positionId != null) {
                            user.setPositionId(positionId);
                        }
                        if (positionTitle != null) {
                            user.setPositionTitle(positionTitle);
                        }
                        
                        // Password: use from CSV if provided, otherwise leave NULL for user to set on first login
                        String password = null;
                        if (passwordColumn != null && record.isMapped(passwordColumn)) {
                            String passwordValue = record.get(passwordColumn).trim();
                            if (!passwordValue.isEmpty()) {
                                password = passwordValue;
                            }
                        }
                        
                        if (password != null) {
                            user.setPassword(passwordEncoder.encode(password));
                        } else {
                            user.setPassword(null); // User must set password on first login
                        }
                        
                        // Role: use from CSV if provided, otherwise default to USER
                        if (roleColumn != null && record.isMapped(roleColumn)) {
                            String roleValue = record.get(roleColumn).trim();
                            if (!roleValue.isEmpty()) {
                                try {
                                    user.setRole(Role.valueOf(roleValue.toUpperCase()));
                                } catch (IllegalArgumentException e) {
                                    user.setRole(Role.USER);
                                }
                            } else {
                                user.setRole(Role.USER);
                            }
                        } else {
                            user.setRole(Role.USER);
                        }
                        
                        // Set default access permissions (null = false)
                        user.setCompanyAssignmentsAccess(null);
                        user.setAcademicUnitAssignmentsAccess(null);
                        user.setGiftAssignmentsAccess(null);
                        
                        // Save user in separate transaction to avoid rollback affecting other records
                        if (saveUserInNewTransaction(user)) {
                            importedCount++;
                            if (importedCount % 100 == 0) {
                                logger.debug("Progress: {} users imported so far", importedCount);
                            }
                        } else {
                            skippedCount++;
                            saveErrorCount++;
                            logger.error("SKIP [Line {}]: Failed to save user. Email: '{}', EmployeeID: '{}', FirstName: '{}', LastName: '{}'", 
                                recordNumber, email, employeeId, firstName, lastName);
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
            logger.info("  - Complete duplicate records (all fields match): {}", duplicateRecordCount);
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
    public boolean saveUserInNewTransaction(User user) {
        try {
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            // Log error but don't throw - return false to indicate failure
            logger.error("Error saving user in transaction - Email: '{}', EmployeeID: '{}', Error: {}", 
                user.getEmail(), user.getEmployeeId(), e.getMessage(), e);
            return false;
        }
    }
    
    public Map<String, Long> getUserStatistics() {
        Map<String, Long> stats = new java.util.HashMap<>();
        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(Role.ADMIN);
        long userCount = userRepository.countByRole(Role.USER);
        
        stats.put("total", totalUsers);
        stats.put("admin", adminCount);
        stats.put("user", userCount);
        
        return stats;
    }
    
    private UserDTO convertToDTO(User user) {
        // Los getters ya manejan null como false
        return new UserDTO(
                user.getId(),
                user.getEmployeeId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPositionId(),
                user.getPositionTitle(),
                user.getEmail(),
                user.getRole(),
                user.getCompanyAssignmentsAccess(),
                user.getAcademicUnitAssignmentsAccess(),
                user.getGiftAssignmentsAccess()
        );
    }
}

