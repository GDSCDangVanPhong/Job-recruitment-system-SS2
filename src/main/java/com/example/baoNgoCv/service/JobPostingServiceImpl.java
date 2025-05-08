package com.example.baoNgoCv.service;

import com.example.baoNgoCv.dao.ApplicantRepository;
import com.example.baoNgoCv.dao.JobPostingRepository;
import com.example.baoNgoCv.dao.JobSavedRepository;
import com.example.baoNgoCv.dao.NotificationRepository;
import com.example.baoNgoCv.model.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JobPostingServiceImpl implements JobPostingService {


    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private UserService userService;
    @Autowired
    private ApplicantRepository applicantRepository;
    @Autowired
    private JobSavedRepository jobSavedRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationService notificationService;

    @Override
    public List<JobPosting> getAll(int page) {
        Pageable pageable = PageRequest.of(page, 9);
        Page<JobPosting> jobPostingPage = jobPostingRepository.findAll(pageable);
        return jobPostingPage.getContent();
    }

    @Override
    public List<JobPosting> getJobPostingByIndustryId(Long industryId) {
        List<JobPosting> jobPostingList = jobPostingRepository.findByIndustryId(industryId);
        return jobPostingList;
    }

    @Override
    public List<JobPosting> getByCompanyId(Long id) {
        return jobPostingRepository.findByCompanyId(id);
    }

    @Override
    public Page<JobPosting> searchJobPostings(String keyword, String location, String experience, String salaryRange, int page) {
        Pageable pageable = PageRequest.of(page, 6);
        return jobPostingRepository.searchJobPostings(keyword, location, experience, salaryRange, pageable);
    }


    public boolean validateJobPosting(Map<String, String> formData, Map<String, String> response, long id) {
        // Kiểm tra các trường bắt buộc không được null hoặc rỗng
        System.out.println("formData: " + formData);
        Company company = userService.getCurrentCompany();
        if (id == 0) {
            response.put("status", "error");
            response.put("message", "Job posting ID is required and cannot be 0!");
            return false;
        }
        if (company == null) {
            response.put("status", "error");
            response.put("message", "Company name is required!");
            return false;
        }

        // Kiểm tra các trường bắt buộc
        String[] requiredFields = {"title", "jobType", "location", "category", "experience", "salary", "description", "deadline"};
        for (String field : requiredFields) {
            if (formData.get(field) == null || formData.get(field).isEmpty()) {
                response.put("status", "error");
                response.put("message", field + " is required!");
                return false;
            }
        }

        // Lấy JobPosting theo id
        Optional<JobPosting> optionalJobPosting = getJobPostingById(id);
        if (!optionalJobPosting.isPresent()) {
            response.put("status", "error");
            response.put("message", "Job posting not found!");
            return false;
        }

        JobPosting jobPosting = optionalJobPosting.get();
        LocalDate postedDate = jobPosting.getPostedDate(); // Lấy ngày đăng tin từ JobPosting

        String applicationDeadlineStr = formData.get("deadline");

        try {
            // Chuyển đổi ngày hết hạn từ chuỗi sang LocalDate
            LocalDate applicationDeadline = LocalDate.parse(applicationDeadlineStr);

            // Kiểm tra ngày đăng tin không được lớn hơn ngày hiện tại
            LocalDate currentDate = LocalDate.now();
            if (postedDate.isAfter(currentDate)) {
                response.put("status", "error");
                response.put("message", "Posted date cannot be in the future!");
                return false;
            }

            // Tính số ngày giữa hai ngày
            long diffInDays = java.time.temporal.ChronoUnit.DAYS.between(postedDate, applicationDeadline);

            if (diffInDays < 7) {
                response.put("status", "error");
                response.put("message", "Application deadline must be at least 7 days after posted date!");
                return false;
            }


        } catch (DateTimeParseException e) {
            response.put("status", "error");
            response.put("message", "Invalid date format! Please use yyyy-MM-dd.");
            return false;
        }

        return true;
    }

    @Override
    public void updateJobPostingStatus(Long jobPostingId, JobPostingStatus jobPostingStatus) {
        // Tìm kiếm JobPosting theo jobPostingId
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new RuntimeException("Job posting not found with id: " + jobPostingId));

        // Kiểm tra trạng thái hiện tại của JobPosting
        if (jobPosting.getStatus() == JobPostingStatus.EXPIRED) {
            throw new IllegalStateException("Cannot update job posting status as it is already " + jobPosting.getStatus());
        }
        // Nếu trạng thái cần cập nhật là EXPIRED, tìm tất cả ứng viên đã lưu job này
        if (jobPostingStatus == JobPostingStatus.EXPIRED) {
            // Tìm tất cả các ứng viên đã ứng tuyển cho jobPosting này (giả sử có mối quan hệ với bảng Applicant)
            List<JobSaved> jobSavedList = jobSavedRepository.findByJobPosting(jobPosting);

            // Gửi thông báo cho tất cả ứng viên đã ứng tuyển
            for (JobSaved jobSaved : jobSavedList) {
                User user = jobSaved.getUser();
                Notification notification = new Notification();
                notification.setTitle("Job posting in your saved list expired");
                notification.setSenderUser(userService.findByUsername("admin01"));
                notification.setCreatedAt(LocalDateTime.now());
                notification.setRecipientUser(user);
                notification.setType(NotificationType.JOB_CLOSED);
                notification.setAvatar(userService.findByUsername("admin01").getProfilePicture());
                notification.setRead(false);
                Notification newNoti = notificationRepository.save(notification);
                newNoti.setHref("/jobseeker/job-save?notiId=" + newNoti.getId());
                notificationRepository.save(newNoti);
                notificationService.sendReviewNotificationToUser("Job posting in your saved list expired", user, userService.findByUsername("admin01").getProfilePicture(), "Admin", "/jobseeker/job-save?notiId=" + newNoti.getId());


            }


        }
        // Cập nhật trạng thái của JobPosting
        jobPosting.setStatus(jobPostingStatus);

        // Lưu lại JobPosting với trạng thái đã cập nhật
        jobPostingRepository.save(jobPosting);
    }


    @Override
    public Optional<JobPosting> getJobPostingById(long id) {
        return jobPostingRepository.findById(id);
    }


}
