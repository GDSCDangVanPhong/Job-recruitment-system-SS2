package com.example.baoNgoCv.dto;

import java.time.LocalDate;
import java.util.List;

public class JobPostingDTO {

    private String title;
    private String companyName;
    private String location;
    private String jobType;
    private String category;
    private String experience;
    private String salary;
    private String description;
    private List<String> requirements;
    private String benefits;
    private LocalDate deadline;
    private Integer  maxApplicants;

    public JobPostingDTO(String title, String companyName, String location, String jobType, String category, String experience, String salary, String description, List<String> requirements, String benefits, LocalDate deadline, int maxApplicants) {
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.jobType = jobType;
        this.category = category;
        this.experience = experience;
        this.salary = salary;
        this.description = description;
        this.requirements = requirements;
        this.benefits = benefits;
        this.deadline = deadline;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public int getMaxApplicants() {
        return maxApplicants;
    }

    public void setMaxApplicants(int maxApplicants) {
        this.maxApplicants = maxApplicants;
    }
}
