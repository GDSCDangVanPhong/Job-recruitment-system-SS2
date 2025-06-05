package com.example.baoNgoCv.dto;

import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.Set;

public class CompanyDTO {



    @NotBlank(message = "Username is required")
    @Size(min = 6, max = 21, message = "Username must be between 6 and 21 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotNull(message = "Company name cannot be null")
    @Size(min = 1, max = 100, message = "Company name must be between 1 and 100 characters")
    private String name;


    @NotNull(message = "Location cannot be null")
    @Size(min = 1, max = 100, message = "Location must be between 1 and 100 characters")
    private String location;

    @Email(message = "Contact email must be a valid email address")
    private String contactEmail;



    // Constructors
    public CompanyDTO() {
    }



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }



    public @NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters long") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password is required") @Size(min = 6, message = "Password must be at least 6 characters long") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Confirm Password is required") String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(@NotBlank(message = "Confirm Password is required") String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}