package com.pwc.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        response.setLocationAssignmentsAccess(user.getLocationAssignmentsAccess());
        response.setProjectAssignmentsAccess(user.getProjectAssignmentsAccess());
        response.setGrantAssignmentsAccess(user.getGrantAssignmentsAccess());
        response.setPaygroupAssignmentsAccess(user.getPaygroupAssignmentsAccess());
        
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
    
    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        // Check if email already exists
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            throw new RuntimeException("A user with this email already exists");
        }
        
        User user = new User();
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setCompany(userCreateDTO.getCompany());
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
        user.setLocationAssignmentsAccess(userCreateDTO.getLocationAssignmentsAccess());
        user.setProjectAssignmentsAccess(userCreateDTO.getProjectAssignmentsAccess());
        user.setGrantAssignmentsAccess(userCreateDTO.getGrantAssignmentsAccess());
        user.setPaygroupAssignmentsAccess(userCreateDTO.getPaygroupAssignmentsAccess());
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    
    @Transactional
    public UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if email is being changed and if it already exists
        if (userUpdateDTO.getEmail() != null && !userUpdateDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userUpdateDTO.getEmail())) {
                throw new RuntimeException("A user with this email already exists");
            }
        }
        
        if (userUpdateDTO.getFirstName() != null) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.getLastName() != null) {
            user.setLastName(userUpdateDTO.getLastName());
        }
        if (userUpdateDTO.getCompany() != null) {
            user.setCompany(userUpdateDTO.getCompany());
        }
        if (userUpdateDTO.getEmail() != null) {
            user.setEmail(userUpdateDTO.getEmail());
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
        if (userUpdateDTO.getLocationAssignmentsAccess() != null) {
            user.setLocationAssignmentsAccess(userUpdateDTO.getLocationAssignmentsAccess());
        }
        if (userUpdateDTO.getProjectAssignmentsAccess() != null) {
            user.setProjectAssignmentsAccess(userUpdateDTO.getProjectAssignmentsAccess());
        }
        if (userUpdateDTO.getGrantAssignmentsAccess() != null) {
            user.setGrantAssignmentsAccess(userUpdateDTO.getGrantAssignmentsAccess());
        }
        if (userUpdateDTO.getPaygroupAssignmentsAccess() != null) {
            user.setPaygroupAssignmentsAccess(userUpdateDTO.getPaygroupAssignmentsAccess());
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
        return new UserDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getCompany(),
                user.getEmail(),
                user.getRole(),
                user.getCompanyAssignmentsAccess(),
                user.getAcademicUnitAssignmentsAccess(),
                user.getGiftAssignmentsAccess(),
                user.getLocationAssignmentsAccess(),
                user.getProjectAssignmentsAccess(),
                user.getGrantAssignmentsAccess(),
                user.getPaygroupAssignmentsAccess()
        );
    }
}
