package com.hospitalopd.controller;

import com.hospitalopd.model.Gender;
import com.hospitalopd.model.Medicine;
import com.hospitalopd.model.OpdVisit;
import com.hospitalopd.model.Patient;
import com.hospitalopd.service.OpdService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OpdController {

    private final OpdService opdService;

    public OpdController(OpdService opdService) {
        this.opdService = opdService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("visits", opdService.listVisits());
        model.addAttribute("registeredCount", opdService.registeredCount());
        model.addAttribute("pretestDoneCount", opdService.pretestDoneCount());
        model.addAttribute("checkupDoneCount", opdService.checkupDoneCount());
        return "dashboard";
    }

    @GetMapping("/nurse")
    public String nurseView(Model model) {
        model.addAttribute("visits", opdService.listNurseQueue());
        return "nurse-view";
    }

    @GetMapping("/doctor")
    public String doctorView(Model model) {
        model.addAttribute("visits", opdService.listDoctorQueue());
        return "doctor-view";
    }

    @GetMapping("/register")
    public String registrationForm(Model model) {
        addRegistrationModel(model, new Patient(), new OpdVisit());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("patient") Patient patient,
                           BindingResult patientResult,
                           @Valid @ModelAttribute("visit") OpdVisit visit,
                           BindingResult visitResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (patientResult.hasErrors() || visitResult.hasErrors()) {
            addRegistrationModel(model, patient, visit);
            return "register";
        }

        OpdVisit savedVisit = opdService.registerVisit(patient, visit);
        redirectAttributes.addFlashAttribute("message", "Patient registered with OPD token #" + savedVisit.getId());
        return "redirect:/";
    }

    @GetMapping("/visits/{visitId}")
    public String visitDetails(@PathVariable Long visitId, Model model) {
        model.addAttribute("visit", opdService.getVisit(visitId));
        return "visit-details";
    }

    @GetMapping("/nurse/visits/{visitId}/pretest")
    public String pretestForm(@PathVariable Long visitId, Model model) {
        model.addAttribute("visit", opdService.getVisit(visitId));
        return "pretest";
    }

    @PostMapping("/nurse/visits/{visitId}/pretest")
    public String savePretest(@PathVariable Long visitId,
                              @ModelAttribute("visit") OpdVisit visit,
                              RedirectAttributes redirectAttributes) {
        opdService.savePretest(visitId, visit);
        redirectAttributes.addFlashAttribute("message", "Nurse pre-test saved for OPD token #" + visitId);
        return "redirect:/nurse";
    }

    @GetMapping("/doctor/visits/{visitId}/checkup")
    public String checkupForm(@PathVariable Long visitId, Model model) {
        OpdVisit visit = opdService.getVisit(visitId);
        model.addAttribute("visit", visit);
        model.addAttribute("medicines", opdService.listActiveMedicines());
        model.addAttribute("selectedMedicineIds", selectedMedicineIds(visit));
        return "checkup";
    }

    @PostMapping("/doctor/visits/{visitId}/checkup")
    public String saveCheckup(@PathVariable Long visitId,
                              @ModelAttribute("visit") OpdVisit visit,
                              @RequestParam(value = "medicineIds", required = false) List<Long> medicineIds,
                              RedirectAttributes redirectAttributes) {
        opdService.saveDoctorCheckup(visitId, visit, medicineIds);
        redirectAttributes.addFlashAttribute("message", "Doctor checkup completed for OPD token #" + visitId);
        return "redirect:/doctor";
    }

    private void addRegistrationModel(Model model, Patient patient, OpdVisit visit) {
        model.addAttribute("patient", patient);
        model.addAttribute("visit", visit);
        model.addAttribute("genders", Gender.values());
    }

    private Set<Long> selectedMedicineIds(OpdVisit visit) {
        return visit.getSelectedMedicines().stream()
                .map(Medicine::getId)
                .collect(Collectors.toSet());
    }
}
