package com.example.baoNgoCv.Config;

import com.example.baoNgoCv.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();

    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserService userService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;

    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/main/home", "/status/**", "/user/register", "/user/login", "/jobseeker/job-detail/**", "/jobseeker/company-detail/**", "/jobseeker/job-search", "/company/account", "/company/verify-email","/jobseeker/load-more-job-search","/user/forget-password","/user/check-username","/user/send-email-code-forget-password/**","/user/password-forget-final-email","/user/forget-password-final","/user/forget-password-final","/jobseeker/job-search-live","/picon/mail","/user/test").permitAll()
                                .requestMatchers(HttpMethod.POST, "/user/verify-email","/picon/mail/contact","picon/mail/application").permitAll()
                                .requestMatchers("/jobseeker/password-changing-final", "/jobseeker/password-changing-final-email", "/jobseeker/send-email-code",  "/jobseeker/profile-update", "/jobseeker/edit-education", "/jobseeker/education-update/**", "/jobseeker/job-ex-add", "/jobseeker//job-following", "/jobseeker/education-update", "/jobseeker/job-ex-update", "/jobseeker/my-application", "/jobseeker/apply-job/**","/jobseeker/applicant-review-detail/**","/jobseeker/job-save","/jobseeker/save-job/**","/jobseeker/unsave-job/**").hasRole("USER")
                                .requestMatchers("/company/update-job-posting-status/","/company/profile","/company/profile-update","/company/company-information-update","/company/contact-information-update","/company/post-a-job","/company/jobposting-managing","/company/job-application-list","/company/job-application-detail/**","/company/approve/**","/company/reject/**","/company/send-review/**","/company/update-job-posting/**","/company/delete-job-posting/**","/company/applicant-viewing/**").hasRole("COMPANY")

                                .anyRequest().authenticated()
                ).exceptionHandling(exception -> exception
                        .accessDeniedPage("/403") // Chỉ định trang lỗi 403
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginProcessingUrl("/login-action")
                                .loginPage("/user/login").permitAll()
                                .defaultSuccessUrl("/main/home", true)

                                .usernameParameter("username")
                                .passwordParameter("password")
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout-action").permitAll()
                                .logoutSuccessUrl("/main/home")

                )
        ;
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/img/**");
    }


}
