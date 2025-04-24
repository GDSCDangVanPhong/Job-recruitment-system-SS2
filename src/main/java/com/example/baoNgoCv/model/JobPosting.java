package com.example.baoNgoCv.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "job_posting")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    @JsonBackReference
    private Company company;

    @Column(name = "title")
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "location")
    private String location;


    @Column(name = "job_type")
    private String jobType;

    @Column(name = "salary_range")
    private String salaryRange;

    @Column(name = "posted_date")
    private LocalDate postedDate;

    @Column(name = "application_deadline")
    private LocalDate applicationDeadline;

    @Column(name = "status")
    @Enumerated(EnumType.STRING) // Lưu enum dưới dạng chuỗi trong database
    private JobPostingStatus status;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Requirement> requirements;

    @Column(name = "benefit", length = 1000)
    private String benefit;

    @Column(name = "experience")
    private String experience;

    @ManyToOne
    @JoinColumn(name = "industry_id")
    private Industry industry;

    @Column(name = "applicant_count")
    private Integer applicantCount; // Số lượng ứng viên tối đa cần tuyển dụng

    @Column(name = "max_applicants")
    private Integer maxApplicants; // Số lượng ứng viên tối đa cần tuyển dụng
    @OneToMany(mappedBy = "jobPosting", fetch = FetchType.LAZY)
    private List<Applicant> applicants;  // Thêm danh sách ứng viên đã ứng tuyển vào công việc

    @OneToMany(mappedBy = "jobPosting", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<JobSaved> savedJobs;  // Danh sách các công việc đã được người dùng lưu

    public JobPosting() {
        this.applicantCount = 0;
    }

    public JobPosting(Company company, String title, String description, String location, Industry industry, String jobType, String salaryRange, LocalDate postedDate, LocalDate applicationDeadline, JobPostingStatus status, LocalDate createdAt, LocalDate updatedAt, String experience, int maxApplicants) {
        this.company = company;
        this.title = title;
        this.description = description;
        this.location = location;
        this.industry = industry;
        this.jobType = jobType;
        this.salaryRange = salaryRange;
        this.postedDate = postedDate;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.experience = experience;
        this.maxApplicants = maxApplicants;
        this.applicantCount = 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Industry getIndustry() {
        return industry;
    }

    public void setIndustry(Industry industry) {
        this.industry = industry;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getSalaryRange() {
        return salaryRange;
    }

    public void setSalaryRange(String salaryRange) {
        this.salaryRange = salaryRange;
    }

    public LocalDate getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDate postedDate) {
        this.postedDate = postedDate;
    }

    public LocalDate getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(LocalDate applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public JobPostingStatus getStatus() {
        return status;
    }

    public void setStatus(JobPostingStatus status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(Set<Requirement> requirements) {
        this.requirements = requirements;
    }

    public String getBenefit() {
        return benefit;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public List<String> getRequirementDescriptions() {
        List<String> descriptions = new ArrayList<>();
        for (Requirement requirement : requirements) {
            descriptions.add(requirement.getDescription());
        }
        return descriptions;
    }

    public int getMaxApplicants() {
        return maxApplicants;
    }

    public void setMaxApplicants(int maxApplicants) {
        this.maxApplicants = maxApplicants;
    }

    public Integer getApplicantCount() {
        return applicantCount;
    }

    public void setApplicantCount(Integer applicantCount) {
        this.applicantCount = applicantCount;
    }

    public void incrementApplicantCount() {
        if (this.applicantCount == null) {
            this.applicantCount = 0; // Khởi tạo nếu chưa có giá trị
        }
        this.applicantCount += 1;
    }

    public void decrementApplicantCount() {
        if (this.applicantCount == null || this.applicantCount <= 0) {
            this.applicantCount = 0; // Đảm bảo không giảm dưới 0
        } else {
            this.applicantCount -= 1; // Giảm giá trị nếu lớn hơn 0
        }
    }

    public List<Applicant> getApplicants() {
        return applicants;
    }

    public void setApplicants(List<Applicant> applicants) {
        this.applicants = applicants;
    }

    public List<JobSaved> getSavedJobs() {
        return savedJobs;
    }

    public void setSavedJobs(List<JobSaved> savedJobs) {
        this.savedJobs = savedJobs;
    }
}
