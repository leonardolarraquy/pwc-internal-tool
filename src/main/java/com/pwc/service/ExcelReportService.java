package com.pwc.service;

import com.pwc.model.Assignment;
import com.pwc.model.Employee;
import com.pwc.repository.AssignmentRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service to generate Excel reports using hardcoded format configuration.
 * The format is defined in ExcelFormatConfig class, which was generated
 * once by analyzing the template Excel file.
 */
@Service
public class ExcelReportService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private final AssignmentRepository assignmentRepository;
    
    public ExcelReportService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }
    
    public byte[] generateFullReport() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            // Apply Overview sheet format (from hardcoded config)
            var overviewSheet = workbook.createSheet("Overview");
            ExcelFormatConfig.applyOverviewSheetFormat(workbook, overviewSheet);
            
            // Apply Assign Roles sheet format - first 5 rows with headers (from hardcoded config)
            var assignRolesSheet = workbook.createSheet("Assign Roles");
            ExcelFormatConfig.applyAssignRolesSheetFormat(workbook, assignRolesSheet);
            
            // Populate data rows starting from row 6
            populateAssignRolesData(workbook, assignRolesSheet);
            
            // Write to byte array
            workbook.write(outputStream);
            
            return outputStream.toByteArray();
        }
    }
    
    /**
     * Populate the Assign Roles sheet with assignment data.
     * 
     * Column mapping:
     * - B (1): Spreadsheet Key* - Auto-increment starting at 1
     * - C (2): Effective Date - assignment.createdAt formatted as YYYY-MM-DD
     * - E (4): Event Target Assignee* - employee.positionId
     * - G (6): Row ID* - Auto-increment per positionId (resets when positionId changes)
     * - L (11): Assignable Role* - employee.positionTitle
     * - O (14): Assignees to Add+ - employee.positionId
     */
    private void populateAssignRolesData(XSSFWorkbook workbook, Sheet sheet) {
        // Fetch all assignments ordered by positionId
        List<Assignment> assignments = assignmentRepository.findAllForReport();
        
        if (assignments.isEmpty()) {
            return;
        }
        
        // Get data row style
        CellStyle dataStyle = ExcelFormatConfig.createDataRowStyle(workbook);
        
        // Start at row 6 (index 5)
        int rowIndex = ExcelFormatConfig.getAssignRolesDataStartRow();
        
        // Counters
        int spreadsheetKey = 1;
        int rowIdCounter = 1;
        String previousPositionId = null;
        
        for (Assignment assignment : assignments) {
            Employee employee = assignment.getEmployee();
            String currentPositionId = employee.getPositionId();
            
            // Reset rowIdCounter when positionId changes
            if (previousPositionId != null && !currentPositionId.equals(previousPositionId)) {
                rowIdCounter = 1;
            }
            
            // Create data row
            Row row = sheet.createRow(rowIndex);
            
            // B (1): Spreadsheet Key*
            Cell cellB = row.createCell(1);
            cellB.setCellValue(spreadsheetKey);
            cellB.setCellStyle(dataStyle);
            
            // C (2): Effective Date
            Cell cellC = row.createCell(2);
            if (assignment.getCreatedAt() != null) {
                cellC.setCellValue(assignment.getCreatedAt().format(DATE_FORMATTER));
            }
            cellC.setCellStyle(dataStyle);
            
            // E (4): Event Target Assignee*
            Cell cellE = row.createCell(4);
            cellE.setCellValue(currentPositionId != null ? currentPositionId : "");
            cellE.setCellStyle(dataStyle);
            
            // G (6): Row ID*
            Cell cellG = row.createCell(6);
            cellG.setCellValue(rowIdCounter);
            cellG.setCellStyle(dataStyle);
            
            // L (11): Assignable Role*
            Cell cellL = row.createCell(11);
            cellL.setCellValue(employee.getPositionTitle() != null ? employee.getPositionTitle() : "");
            cellL.setCellStyle(dataStyle);
            
            // O (14): Assignees to Add+
            Cell cellO = row.createCell(14);
            cellO.setCellValue(currentPositionId != null ? currentPositionId : "");
            cellO.setCellStyle(dataStyle);
            
            // Increment counters
            spreadsheetKey++;
            rowIdCounter++;
            previousPositionId = currentPositionId;
            rowIndex++;
        }
    }
}
