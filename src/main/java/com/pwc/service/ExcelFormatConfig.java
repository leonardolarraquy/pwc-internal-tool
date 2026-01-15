package com.pwc.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;

/**
 * Excel format configuration based on template analysis.
 * Template: Excels/Assign_Roles_20251215_1030.xlsx
 * 
 * This class contains the hardcoded format, colors, and structure
 * extracted from analyzing the first rows of the template.
 * 
 * Analysis performed on: Overview (10 rows), Assign Roles (5 rows)
 */
public class ExcelFormatConfig {
    
    // ==================== COLORS (RGB values from template) ====================
    
    // Dark Blue: #000066 - Used for title text and dark blue section headers
    public static final byte[] COLOR_DARK_BLUE = new byte[]{(byte)0x00, (byte)0x00, (byte)0x66};
    
    // Blue: #333399 - Used for column headers, "All" section backgrounds
    public static final byte[] COLOR_BLUE = new byte[]{(byte)0x33, (byte)0x33, (byte)0x99};
    
    // Green: #75923C - Used for Area/Restrictions/Format/Fields label backgrounds
    public static final byte[] COLOR_GREEN = new byte[]{(byte)0x75, (byte)0x92, (byte)0x3C};
    
    // Yellow: #FFFFCC - Used for Restrictions/Format row backgrounds (with pattern)
    public static final byte[] COLOR_YELLOW = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xCC};
    
    // White: #FFFFFF - Used for text on colored backgrounds
    public static final byte[] COLOR_WHITE = new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF};
    
    // Gray: #808080 - Used for Restrictions/Format row text
    public static final byte[] COLOR_GRAY = new byte[]{(byte)0x80, (byte)0x80, (byte)0x80};
    
    // Black: #000000 - Used for regular text
    public static final byte[] COLOR_BLACK = new byte[]{(byte)0x00, (byte)0x00, (byte)0x00};
    
    // ==================== COLUMN WIDTHS (from template) ====================
    
    // Overview sheet column widths (in Excel units, approx 256 units = 1 character)
    public static final double[] OVERVIEW_COLUMN_WIDTHS = {
        9.11,   // A
        28.55,  // B
        13.0,   // C
        13.0,   // D
        13.0    // E
    };
    
    // Assign Roles sheet column widths
    public static final double[] ASSIGN_ROLES_COLUMN_WIDTHS = {
        9.11,    // A - Area labels
        17.55,   // B - Spreadsheet Key
        15.66,   // C - Effective Date
        15.66,   // D - Effective Timezone
        13.0,    // E - Event Target Assignee
        15.66,   // F - Remove All Role Assignments
        13.0,    // G - Row ID
        44.44,   // H - ID Type (wider for Role Assigner header)
        15.66,   // I - ID Value
        15.66,   // J - Parent ID Type
        13.0,    // K - Parent ID Value
        32.0,    // L - Assignable Role
        15.66,   // M - Remove Existing Assignees
        13.0,    // N - Update Later Dated Assignments
        15.66,   // O - Assignees to Add
        13.0,    // P - Assignees to Remove
        15.66,   // Q - Supervisory Org Manager
        13.0     // R - Remove Supervisory Org Manager
    };
    
    // ==================== ASSIGN ROLES SHEET COLUMN HEADERS ====================
    
    /**
     * Column headers for the Assign Roles sheet (Row 5)
     * These are the actual field names from the template
     */
    public static final String[] ASSIGN_ROLES_FIELD_HEADERS = {
        "Fields",                                                    // A5 (green)
        "Spreadsheet Key*",                                          // B5
        "Effective Date",                                            // C5
        "Effective Timezone",                                        // D5
        "Event Target Assignee*",                                    // E5
        "Remove All Role Assignments for Event Target Assignee",     // F5
        "Row ID*",                                                   // G5
        "ID Type",                                                   // H5
        "ID Value",                                                  // I5
        "Parent ID Type",                                            // J5
        "Parent ID Value",                                           // K5
        "Assignable Role*",                                          // L5
        "Remove Existing Assignees for Assignable Role on Role Assigner", // M5
        "Update Later Dated Assignments",                            // N5
        "Assignees to Add+",                                         // O5
        "Assignees to Remove+",                                      // P5
        "Supervisory Organization Single Assignment Manager",        // Q5
        "Remove Supervisory Organization Single Assignment Manager"  // R5
    };
    
    /**
     * Restrictions for each column (Row 3)
     * Values: Required, Optional, Conditionally Required, Optional. May have multiples
     */
    public static final String[] ASSIGN_ROLES_RESTRICTIONS = {
        "Restrictions",              // A3 (green)
        "Required",                  // B3
        "Optional",                  // C3
        "Optional",                  // D3
        "Required",                  // E3
        "Optional",                  // F3
        "Required",                  // G3
        "Required",                  // H3
        "Required",                  // I3
        "Conditionally Required",    // J3
        "Conditionally Required",    // K3
        "Required",                  // L3
        "Optional",                  // M3
        "Optional",                  // N3
        "Optional. May have multiples", // O3
        "Optional. May have multiples", // P3
        "Optional",                  // Q3
        "Optional"                   // R3
    };
    
    /**
     * Format specifications for each column (Row 4)
     */
    public static final String[] ASSIGN_ROLES_FORMATS = {
        "Format",                           // A4 (green)
        "Text",                             // B4
        "YYYY-MM-DD",                       // C4
        "Time_Zone_ID",                     // D4
        "Position_ID",                      // E4
        "Y/N",                              // F4
        "Text",                             // G4
        "Lookup",                           // H4
        "Text",                             // I4
        "Text",                             // J4
        "External_Supplier_Invoice_Source_ID", // K4
        "Organization_Role_ID",             // L4
        "Y/N",                              // M4
        "Y/N",                              // N4
        "Academic_Affiliate_ID",            // O4
        "Academic_Affiliate_ID",            // P4
        "Academic_Affiliate_ID",            // Q4
        "Y/N"                               // R4
    };
    
    /**
     * Area section headers (Row 2)
     */
    public static final String AREA_HEADER = "Area";
    public static final String ALL_HEADER = "All";
    public static final String ROLE_ASSIGNMENT_DATA_HEADER = "Assign Roles Role Assignment Data+ (All)";
    public static final String ROLE_ASSIGNER_HEADER = "Role Assigner* (All > Assign Roles Role Assignment Data+)";
    
    // ==================== OVERVIEW SHEET COLUMN HEADERS ====================
    
    public static final String[] OVERVIEW_COLUMN_HEADERS = {
        "Business Process",
        "Processing Instruction",
        "Discard on Exit Validation Error",
        "Processing Comment"
    };
    
    // ==================== STYLE CREATION METHODS ====================
    
    /**
     * Create the title style for Overview sheet Row 2 (B2)
     * Font: Calibri, 22pt, bold, dark blue #000066
     */
    public static CellStyle createOverviewTitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 22);
        font.setBold(true);
        font.setColor(new XSSFColor(COLOR_DARK_BLUE, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the description style for Overview sheet Row 4 (B4)
     * Font: Verdana, 10pt, black, wrap text, vertical align top
     */
    public static CellStyle createOverviewDescriptionStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.TOP);
        style.setWrapText(true);
        XSSFFont font = workbook.createFont();
        font.setFontName("Verdana");
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(COLOR_BLACK, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the column header style for Overview sheet Row 6
     * Font: Arial, 10pt, bold, black
     */
    public static CellStyle createOverviewColumnHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(new XSSFColor(COLOR_BLACK, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the title style for Assign Roles sheet Row 1
     * Font: Arial, 14pt, bold, dark blue #000066
     */
    public static CellStyle createAssignRolesTitleStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        font.setColor(new XSSFColor(COLOR_DARK_BLUE, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the green header style (Area, Restrictions, Format, Fields labels)
     * Font: Arial, 8pt, bold, white
     * Fill: solid green #75923C
     * Border: thin all sides
     */
    public static CellStyle createGreenLabelStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(COLOR_GREEN, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorders(style);
        
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 8);
        font.setBold(true);
        font.setColor(new XSSFColor(COLOR_WHITE, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the blue header style (column headers in Row 5, "All" section in Row 2)
     * Font: Arial, 8pt, bold, white
     * Fill: solid blue #333399
     * Border: thin all sides
     */
    public static CellStyle createBlueHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(COLOR_BLUE, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorders(style);
        
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 8);
        font.setBold(true);
        font.setColor(new XSSFColor(COLOR_WHITE, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the dark blue header style (section headers like "Assign Roles Role Assignment Data+")
     * Font: Arial, 8pt, bold, white
     * Fill: solid dark blue #000066
     * Border: thin all sides
     */
    public static CellStyle createDarkBlueHeaderStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(COLOR_DARK_BLUE, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        applyThinBorders(style);
        
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 8);
        font.setBold(true);
        font.setColor(new XSSFColor(COLOR_WHITE, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the restrictions/format row style (Rows 3 and 4)
     * Font: Arial, 8pt, not bold, gray #808080
     * Fill: light horizontal pattern with yellow #FFFFCC foreground, white background
     * Border: thin all sides
     */
    public static CellStyle createRestrictionsFormatStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(new XSSFColor(COLOR_YELLOW, null));
        style.setFillBackgroundColor(new XSSFColor(COLOR_WHITE, null));
        style.setFillPattern(FillPatternType.THIN_HORZ_BANDS);
        applyThinBorders(style);
        
        XSSFFont font = workbook.createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 8);
        font.setBold(false);
        font.setColor(new XSSFColor(COLOR_GRAY, null));
        style.setFont(font);
        return style;
    }
    
    /**
     * Create the data row style for content rows
     * Font: Calibri, 10pt, black
     * Border: thin all sides
     */
    public static CellStyle createDataRowStyle(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        applyThinBorders(style);
        
        XSSFFont font = workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(COLOR_BLACK, null));
        style.setFont(font);
        return style;
    }
    
    // ==================== SHEET BUILDING METHODS ====================
    
    /**
     * Apply format to Overview sheet
     */
    public static void applyOverviewSheetFormat(XSSFWorkbook workbook, Sheet sheet) {
        // Set column widths
        for (int i = 0; i < OVERVIEW_COLUMN_WIDTHS.length; i++) {
            sheet.setColumnWidth(i, (int)(OVERVIEW_COLUMN_WIDTHS[i] * 256));
        }
        
        // Row 1: Title cell (A1) - just the version text, no special formatting
        Row row1 = sheet.createRow(0);
        Cell titleCell1 = row1.createCell(0);
        titleCell1.setCellValue("Assign Roles - v44.0");
        
        // Row 2: Main title (B2)
        Row row2 = sheet.createRow(1);
        row2.setHeightInPoints(28.8f);
        Cell titleCell2 = row2.createCell(1);
        titleCell2.setCellValue("Assign Roles v44.0");
        titleCell2.setCellStyle(createOverviewTitleStyle(workbook));
        
        // Row 3: Empty
        sheet.createRow(2);
        
        // Row 4: Description (B4:E4 merged)
        Row row4 = sheet.createRow(3);
        row4.setHeightInPoints(15f);
        Cell descCell = row4.createCell(1);
        descCell.setCellValue("This operation will assign organization roles to one ore more workers or positions.");
        descCell.setCellStyle(createOverviewDescriptionStyle(workbook));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 1, 4));
        
        // Row 5: Empty
        sheet.createRow(4);
        
        // Row 6: Column headers (B6:E6)
        Row row6 = sheet.createRow(5);
        CellStyle headerStyle = createOverviewColumnHeaderStyle(workbook);
        for (int i = 0; i < OVERVIEW_COLUMN_HEADERS.length; i++) {
            Cell cell = row6.createCell(i + 1);
            cell.setCellValue(OVERVIEW_COLUMN_HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Row 7: Data row example
        Row row7 = sheet.createRow(6);
        Cell cell70 = row7.createCell(0);
        cell70.setCellValue("1");
        Cell cell71 = row7.createCell(1);
        cell71.setCellValue("Assign Roles");
    }
    
    /**
     * Apply format to Assign Roles sheet (header structure only - first 5 rows)
     */
    public static void applyAssignRolesSheetFormat(XSSFWorkbook workbook, Sheet sheet) {
        // Set column widths
        for (int i = 0; i < ASSIGN_ROLES_COLUMN_WIDTHS.length; i++) {
            sheet.setColumnWidth(i, (int)(ASSIGN_ROLES_COLUMN_WIDTHS[i] * 256));
        }
        
        // Prepare styles
        CellStyle titleStyle = createAssignRolesTitleStyle(workbook);
        CellStyle greenStyle = createGreenLabelStyle(workbook);
        CellStyle blueStyle = createBlueHeaderStyle(workbook);
        CellStyle darkBlueStyle = createDarkBlueHeaderStyle(workbook);
        CellStyle restrictionsStyle = createRestrictionsFormatStyle(workbook);
        
        // ========== Row 1: Title (merged A1:R1) ==========
        Row row1 = sheet.createRow(0);
        row1.setHeightInPoints(17.4f);
        Cell titleCell = row1.createCell(0);
        titleCell.setCellValue("Assign Roles - v44.0");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 17)); // A1:R1
        
        // ========== Row 2: Area headers ==========
        Row row2 = sheet.createRow(1);
        
        // A2: Area (green)
        Cell areaCell = row2.createCell(0);
        areaCell.setCellValue(AREA_HEADER);
        areaCell.setCellStyle(greenStyle);
        
        // B2: All (blue, merged B2:F2)
        Cell allCell = row2.createCell(1);
        allCell.setCellValue(ALL_HEADER);
        allCell.setCellStyle(blueStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 1, 5)); // B2:F2
        // Apply style to merged cells
        for (int i = 2; i <= 5; i++) {
            Cell c = row2.createCell(i);
            c.setCellStyle(blueStyle);
        }
        
        // G2: Assign Roles Role Assignment Data+ (dark blue)
        Cell roleDataCell = row2.createCell(6);
        roleDataCell.setCellValue(ROLE_ASSIGNMENT_DATA_HEADER);
        roleDataCell.setCellStyle(darkBlueStyle);
        
        // H2: Role Assigner* (blue, merged H2:K2)
        Cell roleAssignerCell = row2.createCell(7);
        roleAssignerCell.setCellValue(ROLE_ASSIGNER_HEADER);
        roleAssignerCell.setCellStyle(blueStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 7, 10)); // H2:K2
        for (int i = 8; i <= 10; i++) {
            Cell c = row2.createCell(i);
            c.setCellStyle(blueStyle);
        }
        
        // L2: Assign Roles Role Assignment Data+ (dark blue, merged L2:R2)
        Cell roleDataCell2 = row2.createCell(11);
        roleDataCell2.setCellValue(ROLE_ASSIGNMENT_DATA_HEADER);
        roleDataCell2.setCellStyle(darkBlueStyle);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 11, 17)); // L2:R2
        for (int i = 12; i <= 17; i++) {
            Cell c = row2.createCell(i);
            c.setCellStyle(darkBlueStyle);
        }
        
        // ========== Row 3: Restrictions ==========
        Row row3 = sheet.createRow(2);
        for (int i = 0; i < ASSIGN_ROLES_RESTRICTIONS.length; i++) {
            Cell cell = row3.createCell(i);
            cell.setCellValue(ASSIGN_ROLES_RESTRICTIONS[i]);
            if (i == 0) {
                cell.setCellStyle(greenStyle);
            } else {
                cell.setCellStyle(restrictionsStyle);
            }
        }
        
        // ========== Row 4: Format ==========
        Row row4 = sheet.createRow(3);
        for (int i = 0; i < ASSIGN_ROLES_FORMATS.length; i++) {
            Cell cell = row4.createCell(i);
            cell.setCellValue(ASSIGN_ROLES_FORMATS[i]);
            if (i == 0) {
                cell.setCellStyle(greenStyle);
            } else {
                cell.setCellStyle(restrictionsStyle);
            }
        }
        
        // ========== Row 5: Field headers ==========
        Row row5 = sheet.createRow(4);
        for (int i = 0; i < ASSIGN_ROLES_FIELD_HEADERS.length; i++) {
            Cell cell = row5.createCell(i);
            cell.setCellValue(ASSIGN_ROLES_FIELD_HEADERS[i]);
            if (i == 0) {
                cell.setCellStyle(greenStyle);
            } else {
                cell.setCellStyle(blueStyle);
            }
        }
    }
    
    /**
     * Get the starting row index for data (after headers)
     * Data starts at row 6 (index 5) in the Assign Roles sheet
     */
    public static int getAssignRolesDataStartRow() {
        return 5; // 0-indexed, so row 6
    }
    
    /**
     * Get the number of data columns (excluding the "Fields" label column)
     */
    public static int getAssignRolesDataColumnCount() {
        return ASSIGN_ROLES_FIELD_HEADERS.length - 1; // Exclude column A (Fields)
    }
    
    // ==================== HELPER METHODS ====================
    
    private static void applyThinBorders(XSSFCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }
}
