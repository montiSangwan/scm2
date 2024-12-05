package com.scm.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.scm.entities.Contact;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final String NAME = "Name";
    private static final String EMAIL = "Email";
    private static final String PHONE_NUMBER = "Phone Number";
    private static final String ADDRESS = "Address";

    private final ContactService contactService;

    public ExcelExportServiceImpl(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public ResponseEntity<?> generateExcel(HttpServletResponse response) {

        List<Contact> contacts = contactService.getAll();

        // Create a new workbook and sheet with name Contacts
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Contacts");

        // Create a header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {NAME, EMAIL, PHONE_NUMBER, ADDRESS};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Populate the sheet with contact data
        int rowNum = 1; // Start from the second row
        for (Contact contact : contacts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(contact.getName());
            row.createCell(1).setCellValue(contact.getEmail());
            row.createCell(2).setCellValue(contact.getPhoneNumber());
            row.createCell(3).setCellValue(contact.getAddress());
        }

         // Set the response headers for file download
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.xlsx");

        // Write the workbook to the response output stream
        try {
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.flush(); // Ensure all data is written to the output stream
        } catch (IOException e) {
            log.error("Error while writing workbook data into output stream");
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                log.error("Error while closing workbook");
            }
        }

        // Return a success response
        return ResponseEntity.ok("File generated and downloaded successfully.");
    }

}
