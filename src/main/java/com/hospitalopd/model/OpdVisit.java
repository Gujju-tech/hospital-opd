package com.hospitalopd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
public class OpdVisit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @NotBlank
    private String department;

    private String assignedDoctor;

    @Column(length = 1000)
    private String chiefComplaint;

    private LocalDateTime visitDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private VisitStatus status = VisitStatus.REGISTERED;

    private BigDecimal temperatureCelsius;

    private Integer pulseRate;

    private String bloodPressure;

    private BigDecimal weightKg;

    private BigDecimal oxygenSaturation;

    @Column(length = 1000)
    private String nurseNotes;

    @Column(length = 1500)
    private String diagnosis;

    @Column(length = 1500)
    private String prescription;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "opd_visit_medicine",
            joinColumns = @JoinColumn(name = "visit_id"),
            inverseJoinColumns = @JoinColumn(name = "medicine_id")
    )
    private Set<Medicine> selectedMedicines = new LinkedHashSet<>();

    @Column(length = 1000)
    private String doctorAdvice;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAssignedDoctor() {
        return assignedDoctor;
    }

    public void setAssignedDoctor(String assignedDoctor) {
        this.assignedDoctor = assignedDoctor;
    }

    public String getChiefComplaint() {
        return chiefComplaint;
    }

    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }

    public LocalDateTime getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }

    public VisitStatus getStatus() {
        return status;
    }

    public void setStatus(VisitStatus status) {
        this.status = status;
    }

    public BigDecimal getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(BigDecimal temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public Integer getPulseRate() {
        return pulseRate;
    }

    public void setPulseRate(Integer pulseRate) {
        this.pulseRate = pulseRate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public BigDecimal getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(BigDecimal oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    public String getNurseNotes() {
        return nurseNotes;
    }

    public void setNurseNotes(String nurseNotes) {
        this.nurseNotes = nurseNotes;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public Set<Medicine> getSelectedMedicines() {
        return selectedMedicines;
    }

    public void setSelectedMedicines(Set<Medicine> selectedMedicines) {
        this.selectedMedicines = selectedMedicines;
    }

    public String getDoctorAdvice() {
        return doctorAdvice;
    }

    public void setDoctorAdvice(String doctorAdvice) {
        this.doctorAdvice = doctorAdvice;
    }
}
