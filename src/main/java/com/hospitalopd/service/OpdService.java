package com.hospitalopd.service;

import com.hospitalopd.model.Medicine;
import com.hospitalopd.model.OpdVisit;
import com.hospitalopd.model.Patient;
import com.hospitalopd.model.VisitStatus;
import com.hospitalopd.repository.MedicineRepository;
import com.hospitalopd.repository.OpdVisitRepository;
import com.hospitalopd.repository.PatientRepository;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpdService {

    private final PatientRepository patientRepository;
    private final OpdVisitRepository visitRepository;
    private final MedicineRepository medicineRepository;

    public OpdService(PatientRepository patientRepository,
                      OpdVisitRepository visitRepository,
                      MedicineRepository medicineRepository) {
        this.patientRepository = patientRepository;
        this.visitRepository = visitRepository;
        this.medicineRepository = medicineRepository;
    }

    @Transactional(readOnly = true)
    public List<OpdVisit> listVisits() {
        return visitRepository.findAllByOrderByVisitDateDesc();
    }

    @Transactional(readOnly = true)
    public List<OpdVisit> listNurseQueue() {
        return visitRepository.findByStatusOrderByVisitDateDesc(VisitStatus.REGISTERED);
    }

    @Transactional(readOnly = true)
    public List<OpdVisit> listDoctorQueue() {
        return visitRepository.findByStatusOrderByVisitDateDesc(VisitStatus.PRETEST_DONE);
    }

    @Transactional(readOnly = true)
    public List<Medicine> listActiveMedicines() {
        return medicineRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public OpdVisit getVisit(Long visitId) {
        return visitRepository.findById(visitId).orElseThrow(() -> new VisitNotFoundException(visitId));
    }

    @Transactional
    public OpdVisit registerVisit(Patient patient, OpdVisit visit) {
        Patient savedPatient = patientRepository.save(patient);
        visit.setPatient(savedPatient);
        visit.setStatus(VisitStatus.REGISTERED);
        return visitRepository.save(visit);
    }

    @Transactional
    public OpdVisit savePretest(Long visitId, OpdVisit form) {
        OpdVisit visit = getVisit(visitId);
        visit.setTemperatureCelsius(form.getTemperatureCelsius());
        visit.setPulseRate(form.getPulseRate());
        visit.setBloodPressure(form.getBloodPressure());
        visit.setWeightKg(form.getWeightKg());
        visit.setOxygenSaturation(form.getOxygenSaturation());
        visit.setNurseNotes(form.getNurseNotes());
        visit.setStatus(VisitStatus.PRETEST_DONE);
        return visit;
    }

    @Transactional
    public OpdVisit saveDoctorCheckup(Long visitId, OpdVisit form, List<Long> medicineIds) {
        OpdVisit visit = getVisit(visitId);
        visit.setDiagnosis(form.getDiagnosis());
        visit.setPrescription(form.getPrescription());
        visit.setDoctorAdvice(form.getDoctorAdvice());
        Set<Medicine> medicines = new LinkedHashSet<>();
        if (medicineIds != null && !medicineIds.isEmpty()) {
            medicines.addAll(medicineRepository.findAllById(medicineIds));
        }
        visit.setSelectedMedicines(medicines);
        visit.setStatus(VisitStatus.CHECKUP_DONE);
        return visit;
    }

    @Transactional(readOnly = true)
    public long registeredCount() {
        return visitRepository.countByStatus(VisitStatus.REGISTERED);
    }

    @Transactional(readOnly = true)
    public long pretestDoneCount() {
        return visitRepository.countByStatus(VisitStatus.PRETEST_DONE);
    }

    @Transactional(readOnly = true)
    public long checkupDoneCount() {
        return visitRepository.countByStatus(VisitStatus.CHECKUP_DONE);
    }
}
