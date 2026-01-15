package com.pwc.controller;

import com.pwc.service.ExcelReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    private final ExcelReportService excelReportService;
    
    public ReportController(ExcelReportService excelReportService) {
        this.excelReportService = excelReportService;
    }
    
    @GetMapping("/full-report")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateFullReport() {
        try {
            byte[] excelData = excelReportService.generateFullReport();
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
            String filename = "Assign_Roles_" + timestamp + ".xlsx";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.setContentLength(excelData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (IOException e) {
            throw new RuntimeException("Error generating report: " + e.getMessage(), e);
        }
    }
}
