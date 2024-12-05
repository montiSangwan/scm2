package com.scm.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.scm.service.ExcelExportService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user/contacts")
public class ExportContactController {

    private final ExcelExportService excelExportService;

    public ExportContactController(ExcelExportService excelExportService) {
        this.excelExportService = excelExportService;
    }

    @GetMapping("/export-contacts")
    public ResponseEntity<?> exportContacts(HttpServletResponse response) {
        try {
            return excelExportService.generateExcel(response);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
