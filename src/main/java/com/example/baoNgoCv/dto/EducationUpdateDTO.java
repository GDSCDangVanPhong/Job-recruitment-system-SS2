package com.example.baoNgoCv.dto;

import java.time.LocalDate;

public class EducationUpdateDTO {
    private String degree;
    private String institution;
    private LocalDate startDate;
    private LocalDate endDate;
    private String detail;

    public EducationUpdateDTO() {
    }

    public EducationUpdateDTO(String degree, String institution, LocalDate startDate, LocalDate endDate, String detail) {
        this.degree = degree;
        this.institution = institution;
        this.startDate = startDate;
        this.endDate = endDate;
        this.detail = detail;
    }

    // Getters và Setters
    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "EducationUpdateDTO{" +
                "degree='" + degree + '\'' +
                ", institution='" + institution + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", detail='" + detail + '\'' +
                '}';
    }
}
