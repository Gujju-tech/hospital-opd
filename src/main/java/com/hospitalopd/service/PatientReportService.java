package com.hospitalopd.service;

import com.hospitalopd.model.Medicine;
import com.hospitalopd.model.OpdVisit;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class PatientReportService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private final OpdService opdService;

    public PatientReportService(OpdService opdService) {
        this.opdService = opdService;
    }

    public byte[] buildExcelReport() {
        List<OpdVisit> visits = opdService.listVisits();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Patient OPD Details");
            String[] headers = reportHeaders();
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
                header.getCell(i).setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (OpdVisit visit : visits) {
                Row row = sheet.createRow(rowIndex++);
                String[] values = reportValues(visit);
                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i]);
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create Excel report", exception);
        }
    }

    public byte[] buildPdfReport() {
        List<OpdVisit> visits = opdService.listVisits();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A3.rotate(), 24, 24, 24, 24);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            document.add(new Paragraph("Hospital OPD Patient Details", titleFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(reportHeaders().length);
            table.setWidthPercentage(100);
            table.setHeaderRows(1);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8);
            for (String header : reportHeaders()) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setBackgroundColor(java.awt.Color.decode("#dcebe8"));
                table.addCell(cell);
            }

            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 7);
            for (OpdVisit visit : visits) {
                for (String value : reportValues(visit)) {
                    table.addCell(new Phrase(value, bodyFont));
                }
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | IOException exception) {
            throw new IllegalStateException("Unable to create PDF report", exception);
        }
    }

    private String[] reportHeaders() {
        return new String[]{
                "Token",
                "Patient Name",
                "Age",
                "Gender",
                "Phone",
                "Email",
                "Address",
                "Registered At",
                "Visit Date",
                "Department",
                "Assigned Doctor",
                "Status",
                "Chief Complaint",
                "Temperature C",
                "Pulse",
                "Blood Pressure",
                "Weight Kg",
                "SpO2",
                "Nurse Notes",
                "Diagnosis",
                "Medicines",
                "Prescription",
                "Doctor Advice"
        };
    }

    private String[] reportValues(OpdVisit visit) {
        return new String[]{
                text(visit.getId()),
                text(visit.getPatient().getFullName()),
                text(visit.getPatient().getAge()),
                text(visit.getPatient().getGender()),
                text(visit.getPatient().getPhone()),
                text(visit.getPatient().getEmail()),
                text(visit.getPatient().getAddress()),
                dateTime(visit.getPatient().getRegisteredAt()),
                dateTime(visit.getVisitDate()),
                text(visit.getDepartment()),
                text(visit.getAssignedDoctor()),
                text(visit.getStatus()),
                text(visit.getChiefComplaint()),
                text(visit.getTemperatureCelsius()),
                text(visit.getPulseRate()),
                text(visit.getBloodPressure()),
                text(visit.getWeightKg()),
                text(visit.getOxygenSaturation()),
                text(visit.getNurseNotes()),
                text(visit.getDiagnosis()),
                medicineText(visit),
                text(visit.getPrescription()),
                text(visit.getDoctorAdvice())
        };
    }

    private String medicineText(OpdVisit visit) {
        if (visit.getSelectedMedicines().isEmpty()) {
            return "";
        }
        return visit.getSelectedMedicines().stream()
                .map(this::medicineLabel)
                .collect(Collectors.joining("; "));
    }

    private String medicineLabel(Medicine medicine) {
        return text(medicine.getName()) + " " + text(medicine.getStrength()) + " - " + text(medicine.getDefaultDosage());
    }

    private String dateTime(java.time.LocalDateTime dateTime) {
        return dateTime == null ? "" : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString();
    }
}
