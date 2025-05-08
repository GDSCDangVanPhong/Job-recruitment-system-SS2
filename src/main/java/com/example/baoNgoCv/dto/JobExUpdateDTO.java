package com.example.baoNgoCv.dto;

import com.example.baoNgoCv.model.JobExperience;

import java.time.LocalDate;

public class JobExUpdateDTO {

    private Long id;
    private String jobTitle;
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;

    // Constructor
    public JobExUpdateDTO(Long id, String jobTitle, String companyName, LocalDate startDate, LocalDate endDate, String description) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
    }

    // Getter và Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Phương thức ánh xạ từ JobExperience entity sang DTO
    public static JobExUpdateDTO fromEntity(JobExperience jobExperience) {
        return new JobExUpdateDTO(
                jobExperience.getId(),
                jobExperience.getJobTitle(),
                jobExperience.getCompanyName(),
                jobExperience.getStartDate(),
                jobExperience.getEndDate(),
                jobExperience.getDescription()
        );
    }
}
