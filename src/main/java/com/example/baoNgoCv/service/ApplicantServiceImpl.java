package com.example.baoNgoCv.service;

import com.example.baoNgoCv.dao.ApplicationReviewRepository;
import com.example.baoNgoCv.dao.JobPostingRepository;
import com.example.baoNgoCv.dao.NotificationRepository;
import com.example.baoNgoCv.model.*;
import com.example.baoNgoCv.dao.ApplicantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ApplicantServiceImpl implements ApplicantService {

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private FileService fileService;
    @Autowired
    private JobPostingRepository jobPostingRepository;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ApplicationReviewRepository applicationReviewRepository;

    @Override
    public Applicant createApplication(Long jobPostingId, User user, MultipartFile cvUpload, String coverLetter) {
        // Lấy JobPosting từ jobPostingId
        JobPosting jobPosting = jobPostingService.getJobPostingById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found"));

        // Tạo đối tượng Applicant
        Applicant applicant = new Applicant();
        applicant.setUser(user);
        applicant.setJobPosting(jobPosting);
        applicant.setApplicationDate(LocalDateTime.now());
        applicant.setStatus(ApplicationStatus.PENDING); // Trạng thái mặc định

        // Lưu CV nếu có
        if (cvUpload != null && !cvUpload.isEmpty()) {
            String cvFileName = fileService.storeFile(cvUpload); // Chỉ lưu tên file
            applicant.setResume(cvFileName);
        }

        // Lưu cover letter
        applicant.setCoverLetter(coverLetter);

        // Lưu vào cơ sở dữ liệu
        return applicantRepository.save(applicant);
    }

    @Override
    public List<Applicant> getApplicantByUser(User user) {
        // Lấy danh sách công việc của người dùng
        List<Applicant> applicant = applicantRepository.findByUser(user);


        return applicant;
    }

    @Override
    public Boolean approveApplicant(Long applicantId) {
        // Tìm ứng viên bằng ID
        Optional<Applicant> applicant = applicantRepository.findById(applicantId);

        // Nếu không tìm thấy ứng viên, ném ngoại lệ
        Applicant foundApplicant = applicant.orElseThrow(() -> new EntityNotFoundException("Applicant not found with id: " + applicantId));

        // Nếu ứng viên đã được shortlist rồi thì trả về true
        if (foundApplicant.getStatus() == ApplicationStatus.SHORTLISTED) {
            return true; // Nếu đã shortlist rồi, không cần làm gì thêm
        }

        // Kiểm tra trạng thái của jobPosting, nếu là EXPIRED thì ném ngoại lệ
        if (foundApplicant.getJobPosting().getStatus() == JobPostingStatus.EXPIRED) {
            throw new IllegalStateException("Cannot approve applicant. Job posting has expired.");
        }

        // Kiểm tra nếu số lượng ứng viên đã đạt maxApplicant
        JobPosting jobPosting = foundApplicant.getJobPosting();
        int currentApplicantCount = jobPosting.getApplicantCount();

        // Kiểm tra nếu số lượng ứng viên chưa đạt maxApplicant
        if (currentApplicantCount >= jobPosting.getMaxApplicants()) {
            throw new IllegalStateException("Cannot approve applicant. Maximum applicants reached.");
        }

        // Cập nhật trạng thái của ứng viên
        foundApplicant.setStatus(ApplicationStatus.SHORTLISTED);

        // Lưu lại thay đổi vào cơ sở dữ liệu
        applicantRepository.save(foundApplicant);

        // Tăng số lượng ứng viên sau khi lưu ứng viên
        jobPosting.incrementApplicantCount();

        // Kiểm tra xem số lượng ứng viên đã đạt maxApplicant chưa
        if (jobPosting.getApplicantCount() >= jobPosting.getMaxApplicants()) {
            // Nếu số lượng ứng viên đã đạt tối đa, set trạng thái của jobPosting thành "FILLED"
            jobPosting.setStatus(JobPostingStatus.FILLED);
        }
        String message = null;
        // Lưu lại jobPosting một lần duy nhất
        jobPostingRepository.save(jobPosting);
        ApplicationReview applicanApplicationReview = applicationReviewRepository.findByApplicantId(applicantId);
        ApplicationReview newApplicationReview = new ApplicationReview();
        if (applicanApplicationReview == null) {
            message = "Your applicant has been approved!";
            newApplicationReview.setApplicant(foundApplicant);
            newApplicationReview.setReviewComments("There are not any review yet!");
            newApplicationReview.setReviewer(jobPosting.getCompany());
            newApplicationReview.setReviewDate(foundApplicant.getApplicationDate());
        } else {
            message = "The company has updated your review.!";
            newApplicationReview = applicanApplicationReview;
        }

        Notification newNotification = new Notification(message, NotificationType.APPLICATION_ACCEPTED, jobPosting.getCompany().getCompanyLogo());
        newNotification.setSenderCompany(jobPosting.getCompany());
        newNotification.setApplicant(foundApplicant);
        newNotification.setRecipientUser(foundApplicant.getUser());
        Notification notification = notificationRepository.save(newNotification);
        notification.setHref("/notification/applicant-review-detail/" + applicationReviewRepository.save(newApplicationReview).getId() + "?notificationId=" + notification.getId());
        notificationRepository.save(notification);


        notificationService.sendReviewNotificationToUser(message, foundApplicant.getUser(), jobPosting.getCompany().getCompanyLogo(), jobPosting.getCompany().getName(), "/notification/applicant-review-detail/" + applicationReviewRepository.save(newApplicationReview).getId() + "?notificationId=" + notification.getId());


        return true;
    }

    @Override
    public Boolean rejectApplicant(Long applicantId) {
        Optional<Applicant> applicant = applicantRepository.findById(applicantId);
        Applicant foundApplicant = applicant.orElseThrow(() -> new EntityNotFoundException("Applicant not found with id: " + applicantId));

        if (applicant.isEmpty()) {
            throw new IllegalStateException("Applicant is not valid.");
        }
        JobPosting jobPosting = applicant.get().getJobPosting();
        if (applicant.get().getStatus() == ApplicationStatus.SHORTLISTED) {

            jobPosting.decrementApplicantCount();

        }
        if (applicant.get().getJobPosting().getStatus() == JobPostingStatus.FILLED) {
            jobPosting.setStatus(JobPostingStatus.OPEN);

        }
        jobPostingRepository.save(jobPosting);


        String message = null;
        ApplicationReview applicanApplicationReview = applicationReviewRepository.findByApplicantId(applicantId);
        ApplicationReview newApplicationReview = new ApplicationReview();
        if (applicanApplicationReview == null) {
            newApplicationReview.setReviewComments("There are not any review yet!");
            message = "Your applicant has been rejected!";
            newApplicationReview.setReviewer(jobPosting.getCompany());
            newApplicationReview.setApplicant(foundApplicant);
            newApplicationReview.setReviewDate(foundApplicant.getApplicationDate());
        } else {
            newApplicationReview = applicanApplicationReview;
            message = "The company has updated your review.!";
        }


        Notification newNotification = new Notification(message, NotificationType.APPLICATION_REJECTED, jobPosting.getCompany().getCompanyLogo());
        newNotification.setApplicant(foundApplicant);
        newNotification.setSenderCompany(jobPosting.getCompany());
        newNotification.setRecipientUser(foundApplicant.getUser());
        Notification notification = notificationRepository.save(newNotification);
        notification.setHref("/notification/applicant-review-detail/" + applicationReviewRepository.save(newApplicationReview).getId() + "?notificationId=" + notification.getId());
        notificationRepository.save(notification);

        notificationService.sendReviewNotificationToUser(message, foundApplicant.getUser(), jobPosting.getCompany().getCompanyLogo(), jobPosting.getCompany().getName(), "/notification/applicant-review-detail/" + applicationReviewRepository.save(newApplicationReview).getId() + "?notificationId=" + notification.getId());

        // Cập nhật trạng thái của ứng viên
        foundApplicant.setStatus(ApplicationStatus.REJECTED);


        // Lưu lại thay đổi vào cơ sở dữ liệu
        applicantRepository.save(foundApplicant);

        return true;
    }

    @Override
    public boolean existsById(Long applicantId) {
        return applicantRepository.existsById(applicantId);
    }

    @Override
    public User getUserByApplicanId(Long applicantId) {
        return applicantRepository.findUserByApplicantId(applicantId);
    }


}
