package com.example.baoNgoCv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "content_management")  // Tên bảng trong cơ sở dữ liệu
public class ContentManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")  // Tên cột là "id"
    private Long id;

    @Column(name = "title", length = 255)  // Tên cột là "title", độ dài tối đa 255 ký tự
    private String title;

    @Column(name = "body", length = 4000)  // Tên cột là "body", độ dài tối đa 4000 ký tự
    private String body;

    @Column(name = "created_at")  // Tên cột là "created_at"
    private LocalDateTime createdAt;

    @Column(name = "updated_at")  // Tên cột là "updated_at"
    private LocalDateTime updatedAt;

    @Column(name = "status")  // Tên cột là "status"
    private String status;

    // Constructors
    public ContentManagement() {
    }

    public ContentManagement(String title, String body, LocalDateTime createdAt, LocalDateTime updatedAt, String status) {
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
