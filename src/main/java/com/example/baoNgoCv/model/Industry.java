package com.example.baoNgoCv.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Industry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "industry")
    private List<JobPosting> jobPostings;

    public Industry(String name, List<JobPosting> jobPostings) {
        this.name = name;
        this.jobPostings = jobPostings;
    }

    public Industry(String name) {
        this.name = name;
    }

    public Industry() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<JobPosting> getJobPostings() {
        return jobPostings;
    }

    public void setJobPostings(List<JobPosting> jobPostings) {
        this.jobPostings = jobPostings;
    }
}
