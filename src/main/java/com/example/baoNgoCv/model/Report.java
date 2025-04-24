package com.example.baoNgoCv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "report")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "report_type", nullable = false, length = 100)
    private String reportType;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = true)
    private User createdByUser;

    @ManyToOne
    @JoinColumn(name = "created_by_admin_id", nullable = true)
    private Admin createdByAdmin;

    // Constructors
    public Report() {
    }

    public Report(String title, String description, LocalDateTime generatedAt, String content, String reportType, User createdByUser, Admin createdByAdmin) {
        this.title = title;
        this.description = description;
        this.generatedAt = generatedAt;
        this.content = content;
        this.reportType = reportType;
        this.createdByUser = createdByUser;
        this.createdByAdmin = createdByAdmin;
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

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Admin getCreatedByAdmin() {
        return createdByAdmin;
    }

    public void setCreatedByAdmin(Admin createdByAdmin) {
        this.createdByAdmin = createdByAdmin;
    }
}
