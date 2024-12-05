package com.scm.service;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletResponse;

public interface ExcelExportService {
    
    ResponseEntity<?> generateExcel(HttpServletResponse response);
}
