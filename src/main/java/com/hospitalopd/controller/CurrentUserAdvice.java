package com.hospitalopd.controller;

import java.util.Set;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {

    @ModelAttribute
    public void addCurrentUser(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            model.addAttribute("isLoggedIn", false);
            return;
        }

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(java.util.stream.Collectors.toSet());

        model.addAttribute("isLoggedIn", true);
        model.addAttribute("currentUsername", authentication.getName());
        model.addAttribute("canRegister", hasAny(roles, "ROLE_ADMIN", "ROLE_RECEPTION"));
        model.addAttribute("canUseNurseView", hasAny(roles, "ROLE_ADMIN", "ROLE_NURSE"));
        model.addAttribute("canUseDoctorView", hasAny(roles, "ROLE_ADMIN", "ROLE_DOCTOR"));
        model.addAttribute("canDownloadReports", hasAny(roles, "ROLE_ADMIN", "ROLE_RECEPTION"));
    }

    private boolean hasAny(Set<String> roles, String... requiredRoles) {
        for (String requiredRole : requiredRoles) {
            if (roles.contains(requiredRole)) {
                return true;
            }
        }
        return false;
    }
}
