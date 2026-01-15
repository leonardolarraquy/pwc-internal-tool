package com.pwc.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pwc.model.*;
import com.pwc.repository.*;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationTypeRepository organizationTypeRepository;
    private final AssignmentFieldDefinitionRepository fieldDefinitionRepository;
    
    public DataInitializer(UserRepository userRepository, 
                          PasswordEncoder passwordEncoder,
                          OrganizationTypeRepository organizationTypeRepository,
                          AssignmentFieldDefinitionRepository fieldDefinitionRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.organizationTypeRepository = organizationTypeRepository;
        this.fieldDefinitionRepository = fieldDefinitionRepository;
    }
    
    @Override
    public void run(String... args) {
        // Create default admin user if it doesn't exist
        if (!userRepository.findByEmail("admin@pwc.com").isPresent()) {
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setCompany("PWC");
            admin.setEmail("admin@pwc.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@pwc.com / admin123");
        }
        
        // Initialize organization types and field definitions
        initializeOrganizationTypes();
    }
    
    private void initializeOrganizationTypes() {
        // Gift organization type
        if (!organizationTypeRepository.existsByName("Gift")) {
            OrganizationType gift = new OrganizationType("Gift", "gift", "Gift Assignments", "Gift", 1);
            gift = organizationTypeRepository.save(gift);
            
            createFieldDefinition(gift, "finGiftFinancialAnalyst", "FIN_Gift_Financial_Analyst",
                "In Workday I am responsible for preparing or analyzing financial reports for assigned gifts. I support Gift Managers or finance teams with financial data and insights. I maintain and review financial records for gift funds, but do not approve expenses, requisitions, etc.", 1);
            
            createFieldDefinition(gift, "finGiftManager", "FIN_Gift_Manager",
                "In Workday I am responsible for approving transactions impacting the financial results of a gift and upholding donor intent. I am the designated primary manager for specific gifts. I have responsibility for approving spend transactions charged to those gifts. I oversee the financial stewardship and compliance of assigned gift funds.", 2);
            
            createFieldDefinition(gift, "finProfessorshipPartnerGift", "FIN_Professorship_Partner_Gift",
                "In Workday I am responsible for managing or overseeing named professorships funded by specific gifts. I need to review detailed reports on professorship funds and activities. I have authority to approve business processes related to the establishment, management, or modification of named professorships.", 3);
            
            System.out.println("Initialized Gift organization type with field definitions");
        }
        
        // Academic Unit organization type
        if (!organizationTypeRepository.existsByName("Academic Unit")) {
            OrganizationType academicUnit = new OrganizationType("Academic Unit", "academic-unit", "Academic Unit Assignments", "GraduationCap", 2);
            academicUnit = organizationTypeRepository.save(academicUnit);
            
            createFieldDefinition(academicUnit, "hcmAcademicChairAu", "HCM_Academic_Chair_AU",
                "Department Chair role for Academic Unit", 1);
            
            createFieldDefinition(academicUnit, "hcmAcademicDeanAuh", "HCM_Academic_Dean_AUH",
                "Dean role for Academic Unit Hierarchy", 2);
            
            createFieldDefinition(academicUnit, "hcmAcademicFacultyExecutiveAuh", "HCM_Academic_Faculty_Executive_AUH",
                "Faculty Executive role for Academic Unit Hierarchy", 3);
            
            createFieldDefinition(academicUnit, "hcmAcademicFacultyHrAnalystAu", "HCM_Academic_Faculty_HR_Analyst_AU",
                "Faculty HR Analyst role for Academic Unit", 4);
            
            createFieldDefinition(academicUnit, "hcmAcademicProvostPartnerAuh", "HCM_Academic_Provost_Partner_AUH",
                "Provost Partner role for Academic Unit Hierarchy", 5);
            
            createFieldDefinition(academicUnit, "hcmAcademicSchoolDirectorAuh", "HCM_Academic_School_Director_AUH",
                "School Director role for Academic Unit Hierarchy", 6);
            
            System.out.println("Initialized Academic Unit organization type with field definitions");
        }
        
        // Company organization type
        if (!organizationTypeRepository.existsByName("Company")) {
            OrganizationType company = new OrganizationType("Company", "company", "Company Assignments", "Building2", 3);
            organizationTypeRepository.save(company);
            System.out.println("Initialized Company organization type");
        }
        
        // Cost Center organization type
        if (!organizationTypeRepository.existsByName("Cost Center")) {
            OrganizationType costCenter = new OrganizationType("Cost Center", "cost-center", "Cost Center Assignments", "DollarSign", 4);
            organizationTypeRepository.save(costCenter);
            System.out.println("Initialized Cost Center organization type");
        }
        
        // Fund organization type
        if (!organizationTypeRepository.existsByName("Fund")) {
            OrganizationType fund = new OrganizationType("Fund", "fund", "Fund Assignments", "Wallet", 5);
            organizationTypeRepository.save(fund);
            System.out.println("Initialized Fund organization type");
        }
        
        // Location organization type
        if (!organizationTypeRepository.existsByName("Location")) {
            OrganizationType location = new OrganizationType("Location", "location", "Location Assignments", "MapPin", 6);
            organizationTypeRepository.save(location);
            System.out.println("Initialized Location organization type");
        }
        
        // Pay Group organization type
        if (!organizationTypeRepository.existsByName("Pay Group")) {
            OrganizationType payGroup = new OrganizationType("Pay Group", "pay-group", "Pay Group Assignments", "Users", 7);
            organizationTypeRepository.save(payGroup);
            System.out.println("Initialized Pay Group organization type");
        }
        
        // Project organization type
        if (!organizationTypeRepository.existsByName("Project")) {
            OrganizationType project = new OrganizationType("Project", "project", "Project Assignments", "FolderKanban", 8);
            organizationTypeRepository.save(project);
            System.out.println("Initialized Project organization type");
        }
        
        // Grant organization type
        if (!organizationTypeRepository.existsByName("Grant")) {
            OrganizationType grant = new OrganizationType("Grant", "grant", "Grant Assignments", "Award", 9);
            organizationTypeRepository.save(grant);
            System.out.println("Initialized Grant organization type");
        }
        
        // Program organization type
        if (!organizationTypeRepository.existsByName("Program")) {
            OrganizationType program = new OrganizationType("Program", "program", "Program Assignments", "Layers", 10);
            organizationTypeRepository.save(program);
            System.out.println("Initialized Program organization type");
        }
    }
    
    private void createFieldDefinition(OrganizationType orgType, String fieldKey, String fieldTitle, 
                                       String fieldDescription, int displayOrder) {
        if (!fieldDefinitionRepository.existsByOrganizationTypeIdAndFieldKey(orgType.getId(), fieldKey)) {
            AssignmentFieldDefinition fieldDef = new AssignmentFieldDefinition(
                orgType, fieldKey, fieldTitle, fieldDescription, displayOrder);
            fieldDefinitionRepository.save(fieldDef);
        }
    }
}
