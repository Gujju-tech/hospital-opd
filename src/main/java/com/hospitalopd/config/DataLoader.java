package com.hospitalopd.config;

import com.hospitalopd.model.Gender;
import com.hospitalopd.model.Medicine;
import com.hospitalopd.model.OpdVisit;
import com.hospitalopd.model.Patient;
import com.hospitalopd.repository.MedicineRepository;
import com.hospitalopd.service.OpdService;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("local")
public class DataLoader implements CommandLineRunner {

    private final OpdService opdService;
    private final MedicineRepository medicineRepository;

    public DataLoader(OpdService opdService, MedicineRepository medicineRepository) {
        this.opdService = opdService;
        this.medicineRepository = medicineRepository;
    }

    @Override
    public void run(String... args) {
        seedMedicines();

        Patient patient = new Patient();
        patient.setFullName("Asha Sharma");
        patient.setAge(34);
        patient.setGender(Gender.FEMALE);
        patient.setPhone("9876543210");
        patient.setEmail("asha@example.com");
        patient.setAddress("Sector 14, Gurugram");

        OpdVisit visit = new OpdVisit();
        visit.setDepartment("General Medicine");
        visit.setAssignedDoctor("Dr. Meera Rao");
        visit.setChiefComplaint("Fever and body ache for two days");
        visit.setTemperatureCelsius(new BigDecimal("38.1"));
        visit.setPulseRate(92);
        visit.setBloodPressure("118/76");
        visit.setWeightKg(new BigDecimal("62.4"));
        visit.setOxygenSaturation(new BigDecimal("98"));

        opdService.registerVisit(patient, visit);

        Patient doctorReadyPatient = new Patient();
        doctorReadyPatient.setFullName("Ravi Kumar");
        doctorReadyPatient.setAge(41);
        doctorReadyPatient.setGender(Gender.MALE);
        doctorReadyPatient.setPhone("9123456789");
        doctorReadyPatient.setEmail("ravi@example.com");
        doctorReadyPatient.setAddress("Raj Nagar, Ghaziabad");

        OpdVisit doctorReadyVisit = new OpdVisit();
        doctorReadyVisit.setDepartment("Orthopedics");
        doctorReadyVisit.setAssignedDoctor("Dr. Arjun Sen");
        doctorReadyVisit.setChiefComplaint("Knee pain after morning walk");
        OpdVisit savedVisit = opdService.registerVisit(doctorReadyPatient, doctorReadyVisit);

        OpdVisit pretest = new OpdVisit();
        pretest.setTemperatureCelsius(new BigDecimal("36.8"));
        pretest.setPulseRate(78);
        pretest.setBloodPressure("126/82");
        pretest.setWeightKg(new BigDecimal("74.5"));
        pretest.setOxygenSaturation(new BigDecimal("99"));
        pretest.setNurseNotes("Pain score 6/10, no visible swelling.");
        opdService.savePretest(savedVisit.getId(), pretest);
    }

    private void seedMedicines() {
        List<Medicine> medicines = List.of(
                medicine("Paracetamol", "500 mg", "Analgesic / Antipyretic", "1 tablet every 6-8 hours after food", "Avoid overdose; check liver disease history"),
                medicine("Cetirizine", "10 mg", "Antihistamine", "1 tablet at night", "May cause drowsiness"),
                medicine("Amoxicillin", "500 mg", "Antibiotic", "1 capsule three times daily after food", "Check penicillin allergy before prescribing"),
                medicine("Pantoprazole", "40 mg", "Gastric acid reducer", "1 tablet before breakfast", "Review long-term use if repeated"),
                medicine("ORS Sachet", "21 g", "Rehydration", "Mix 1 sachet in 1 litre clean water", "Use prepared solution within 24 hours"),
                medicine("Ibuprofen", "400 mg", "NSAID", "1 tablet after food when needed", "Avoid in gastric ulcer, kidney disease, or late pregnancy")
        );
        medicineRepository.saveAll(medicines);
    }

    private Medicine medicine(String name, String strength, String category, String dosage, String precautions) {
        Medicine medicine = new Medicine();
        medicine.setName(name);
        medicine.setStrength(strength);
        medicine.setCategory(category);
        medicine.setDefaultDosage(dosage);
        medicine.setPrecautions(precautions);
        return medicine;
    }
}
