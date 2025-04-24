package com.example.baoNgoCv.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_review")
public class ApplicationReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "applicant_id", unique = true)
    private Applicant applicant;
    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private Company reviewer; // Người đánh giá, có thể là nhà tuyển dụng hoặc HR



    @Column(name = "review_comment")
    private String reviewComments;


    @Column(name = "review_date")
    private LocalDateTime reviewDate;

    // Constructors
    public ApplicationReview() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public String getReviewComments() {
        return reviewComments;
    }

    public void setReviewComments(String reviewComments) {
        this.reviewComments = reviewComments;
    }

    public LocalDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Company getReviewer() {
        return reviewer;
    }

    public void setReviewer(Company reviewer) {
        this.reviewer = reviewer;
    }
}
