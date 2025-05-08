package com.example.baoNgoCv.dto;

public class JobPostingSearchDTO {
    private Long id;
    private String title;
    private String companyName;
    private String location;
    private String experience;
    private String salary;

    // Constructor to initialize only necessary fields for job listing
    public JobPostingSearchDTO(Long id, String title, String companyName, String location, String experience, String salary) {
        this.id = id;
        this.title = title;
        this.companyName = companyName;
        this.location = location;
        this.experience = experience;
        this.salary = salary;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
