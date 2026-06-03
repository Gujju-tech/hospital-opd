package com.hospitalopd.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**").permitAll()
                        .requestMatchers(PathRequest.toH2Console()).hasRole("ADMIN")
                        .requestMatchers("/register/**").hasAnyRole("ADMIN", "RECEPTION")
                        .requestMatchers("/nurse/**").hasAnyRole("ADMIN", "NURSE")
                        .requestMatchers("/doctor/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers("/reports/**").hasAnyRole("ADMIN", "RECEPTION")
                        .requestMatchers("/", "/visits/**").hasAnyRole("ADMIN", "RECEPTION", "NURSE", "DOCTOR")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                        .logoutSuccessUrl("/login?logout")
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(PathRequest.toH2Console())
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder,
                                                 @Value("${app.security.admin.username}") String adminUsername,
                                                 @Value("${app.security.admin.password}") String adminPassword,
                                                 @Value("${app.security.reception.username}") String receptionUsername,
                                                 @Value("${app.security.reception.password}") String receptionPassword,
                                                 @Value("${app.security.nurse.username}") String nurseUsername,
                                                 @Value("${app.security.nurse.password}") String nursePassword,
                                                 @Value("${app.security.doctor.username}") String doctorUsername,
                                                 @Value("${app.security.doctor.password}") String doctorPassword) {
        UserDetails admin = user(adminUsername, adminPassword, passwordEncoder, "ADMIN", "RECEPTION", "NURSE", "DOCTOR");
        UserDetails reception = user(receptionUsername, receptionPassword, passwordEncoder, "RECEPTION");
        UserDetails nurse = user(nurseUsername, nursePassword, passwordEncoder, "NURSE");
        UserDetails doctor = user(doctorUsername, doctorPassword, passwordEncoder, "DOCTOR");
        return new InMemoryUserDetailsManager(admin, reception, nurse, doctor);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private UserDetails user(String username, String password, PasswordEncoder passwordEncoder, String... roles) {
        return User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles(roles)
                .build();
    }
}
