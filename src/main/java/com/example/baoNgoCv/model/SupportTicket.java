package com.example.baoNgoCv.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_ticket")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private String status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "priority")
    private String priority;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id")
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "created_by_company_id")
    private Company createdByCompany;

    @ManyToOne
    @JoinColumn(name = "assigned_to_id")
    private Admin assignedTo;

    @Column(name = "resolution")
    private String resolution;

    // Constructors
    public SupportTicket() {
    }

    public SupportTicket(String title, String description, String status, LocalDateTime createdAt, LocalDateTime updatedAt, String priority, User createdByUser, Company createdByCompany, Admin assignedTo, String resolution) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.priority = priority;
        this.createdByUser = createdByUser;
        this.createdByCompany = createdByCompany;
        this.assignedTo = assignedTo;
        this.resolution = resolution;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Company getCreatedByCompany() {
        return createdByCompany;
    }

    public void setCreatedByCompany(Company createdByCompany) {
        this.createdByCompany = createdByCompany;
    }

    public Admin getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Admin assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
