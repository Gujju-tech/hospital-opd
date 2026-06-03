package com.hospitalopd.controller;

import com.hospitalopd.service.PatientReportService;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {

    private final PatientReportService patientReportService;

    public ReportController(PatientReportService patientReportService) {
        this.patientReportService = patientReportService;
    }

    @GetMapping("/reports/patients.xlsx")
    public ResponseEntity<byte[]> downloadExcelReport() {
        byte[] report = patientReportService.buildExcelReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment("hospital-opd-patients.xlsx"))
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentLength(report.length)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(report);
    }

    @GetMapping("/reports/patients.pdf")
    public ResponseEntity<byte[]> downloadPdfReport() {
        byte[] report = patientReportService.buildPdfReport();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, attachment("hospital-opd-patients.pdf"))
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .contentLength(report.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(report);
    }

    private String attachment(String fileName) {
        return ContentDisposition.attachment().filename(fileName).build().toString();
    }
}
