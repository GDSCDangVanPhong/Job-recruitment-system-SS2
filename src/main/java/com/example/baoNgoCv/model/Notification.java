package com.example.baoNgoCv.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")  // Ánh xạ trường id với cột id trong cơ sở dữ liệu
    private Long id;

    @Column(name = "message")  // Ánh xạ trường message với cột message trong cơ sở dữ liệu
    private String message;

    @Column(name = "title")  // Ánh xạ trường message với cột message trong cơ sở dữ liệu
    private String title;

    @ManyToOne
    @JoinColumn(name = "sender_user_id", referencedColumnName = "id")
    // "sender_id" là cột trong bảng notification, tham chiếu đến "id" của bảng User
    private User senderUser;  // Người gửi thông báo (có thể là User hoặc Company)

    @ManyToOne
    @JoinColumn(name = "sender_company_id", referencedColumnName = "id")
    // "sender_id" là cột trong bảng notification, tham chiếu đến "id" của bảng User
    private Company senderCompany;  // Người gửi thông báo (có thể là User hoặc Company)

    @Column(name = "created_at") // Ánh xạ trường createdAt với cột created_at trong cơ sở dữ liệu
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "recipient_company_id", referencedColumnName = "id", nullable = true)
    // Tham chiếu đến bảng Company
    private Company recipientCompany;

    @ManyToOne
    @JoinColumn(name = "recipient_user_id", referencedColumnName = "id", nullable = true)  // Tham chiếu đến bảng User
    private User recipientUser;

    @Enumerated(EnumType.STRING)  // Ánh xạ enum thành chuỗi
    @Column(name = "type")
    private NotificationType type;  // Sử dụng enum NotificationType

    @ManyToOne
    @JoinColumn(name = "applicant_id", referencedColumnName = "id", nullable = true)
    private Applicant applicant;  // Ánh xạ với đối tượng Applicant

    @ManyToOne
    @JoinColumn(name = "job_posting_id", referencedColumnName = "id", nullable = true)
    private JobPosting jobPosting;  // Ánh xạ mối quan hệ với JobPosting

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "is_read", nullable = false)  // Ánh xạ với cột is_read trong cơ sở dữ liệu
    private boolean isRead = false;  // Mặc định là chưa đọc

    @Column(name = "href")
    private String href;

    // Constructors
    public Notification() {
    }

    public Notification(String title, NotificationType type, String avatar) {
        this.title = title;
        this.createdAt = createdAt;
        this.type = type;
        this.avatar = avatar;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public User getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(User senderUser) {
        this.senderUser = senderUser;
    }

    public Company getSenderCompany() {
        return senderCompany;
    }

    public void setSenderCompany(Company senderCompany) {
        this.senderCompany = senderCompany;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Company getRecipientCompany() {
        return recipientCompany;
    }

    public void setRecipientCompany(Company recipientCompany) {
        this.recipientCompany = recipientCompany;
    }

    public User getRecipientUser() {
        return recipientUser;
    }

    public void setRecipientUser(User recipientUser) {
        this.recipientUser = recipientUser;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }


    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}