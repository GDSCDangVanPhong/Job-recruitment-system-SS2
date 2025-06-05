package com.example.baoNgoCv.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class PersonalInforUpdateDTO {

    @NotBlank(message = "Full name is required.")
    private String fullName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Phone number is required.")
    private String phone;

    @NotBlank(message = "Location is required.")
    private String location;
    @NotBlank(message = "Date of Birth is required.")
    private LocalDate dateOfBirth;  // Hoặc LocalDate nếu bạn muốn xử lý như đối tượng ngày

    @NotBlank(message = "Nationality is required.")
    private String nationality;

    @NotBlank(message = "Gender is required.")
    private String gender;
    // Getter và Setter
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public @NotBlank(message = "Date of Birth is required.") LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(@NotBlank(message = "Date of Birth is required.") LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public @NotBlank(message = "Gender is required.") String getGender() {
        return gender;
    }

    public void setGender(@NotBlank(message = "Gender is required.") String gender) {
        this.gender = gender;
    }

    public @NotBlank(message = "Nationality is required.") String getNationality() {
        return nationality;
    }

    public void setNationality(@NotBlank(message = "Nationality is required.") String nationality) {
        this.nationality = nationality;
    }


}
