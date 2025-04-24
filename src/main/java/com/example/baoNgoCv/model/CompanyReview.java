package com.example.baoNgoCv.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "company_review")  // Chỉ định tên bảng là "company_review"
public class CompanyReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")  // Tên cột là "id"
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // Tên cột là "user_id", không cho phép giá trị null
    private User user;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)  // Tên cột là "company_id", không cho phép giá trị null
    private Company company;

    @Column(name = "rating")  // Tên cột là "rating"
    private Integer rating;

    @Column(name = "review_text", length = 2000)  // Tên cột là "review_text", độ dài tối đa 2000 ký tự
    private String reviewText;

    @Column(name = "created_at")  // Tên cột là "created_at"
    private LocalDateTime createdAt;

    @Column(name = "updated_at")  // Tên cột là "updated_at"
    private LocalDateTime updatedAt;

    // Constructors
    public CompanyReview() {
    }

    public CompanyReview(User user, Company company, Integer rating, String reviewText, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.user = user;
        this.company = company;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
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
}
