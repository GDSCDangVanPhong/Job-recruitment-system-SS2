package com.example.baoNgoCv.model;

import com.example.baoNgoCv.model.ApplicationStatus;
import com.example.baoNgoCv.model.JobPosting;
import com.example.baoNgoCv.model.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@Table(name = "applicant")
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    @Column(name = "application_date")
    private LocalDateTime applicationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")  // Tên cột là status
    private ApplicationStatus status;

    @Column(name = "resume", length = 1000)  // Tên cột là resume
    private String resume;

    @Column(name = "cover_letter", length = 2000)  // Tên cột là cover_letter
    private String coverLetter;

    @OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private ApplicationReview review;

    // Constructors
    public Applicant() {
    }

    public Applicant(User user, JobPosting jobPosting, LocalDateTime applicationDate, ApplicationStatus status, String resume, String coverLetter) {
        this.user = user;
        this.jobPosting = jobPosting;
        this.applicationDate = applicationDate;
        this.status = status;
        this.resume = resume;
        this.coverLetter = coverLetter;

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

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }

    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }
    public String getFormattedApplicationDate() {
        if (applicationDate != null) {
            // Định dạng ngày giờ, ví dụ: "dd-MM-yyyy HH:mm:ss"
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            return applicationDate.format(formatter);
        }
        return "N/A"; // Nếu applicationDate null, trả về "N/A"
    }
    @Override
    public String toString() {
        return "Applicant{" +
                "user=" + user.getUsername() +
                ", jobPosting=" + jobPosting.getTitle() +
                ", applicationDate=" + applicationDate +
                ", status=" + status +
                ", resume=" + resume +
                ", coverLetter='" + coverLetter + '\'' +
                '}';
    }

    public ApplicationReview getReview() {
        return review;
    }

    public void setReview(ApplicationReview review) {
        this.review = review;
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }
}
