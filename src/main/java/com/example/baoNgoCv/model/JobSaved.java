package com.example.baoNgoCv.model;
import jakarta.persistence.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "saved_jobs")
public class JobSaved {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // Ánh xạ với bảng "users"

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;  // Ánh xạ với bảng "job_posting"

    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    // Constructor mặc định
    public JobSaved() {
    }

    // Constructor có tham số
    public JobSaved(User user, JobPosting jobPosting, LocalDateTime savedAt) {
        this.user = user;
        this.jobPosting = jobPosting;
        this.savedAt = savedAt;
    }

    // Getters và Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }

    public LocalDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(LocalDateTime savedAt) {
        this.savedAt = savedAt;
    }
}