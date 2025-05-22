package com.example.baoNgoCv.service;

import com.example.baoNgoCv.dao.JobPostingRepository;
import com.example.baoNgoCv.dao.NotificationRepository;
import com.example.baoNgoCv.model.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ScheduledService {

    @Autowired
    private JobPostingRepository jobPostingRepository;
    @Autowired
    private JobPostingServiceImpl jobPostingServiceImpl;
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private NotificationService notificationService;

    @Scheduled(cron = "0 53 15 * * *", zone = "Asia/Ho_Chi_Minh")
    public void updateExpiredJobPostings() {
        System.out.println("🔍 Kiểm tra job hết hạn vào lúc: " + LocalDate.now());

        LocalDate today = LocalDate.now();

        // Lấy tất cả job postings từ database
        List<JobPosting> allJobs = jobPostingRepository.findAll();

        for (JobPosting job : allJobs) {
            LocalDate deadline = job.getApplicationDeadline();

            // Kiểm tra nếu deadline đã qua và trạng thái vẫn là OPEN
            if (deadline != null && deadline.isBefore(today) && !(job.getStatus().name() == "EXPIRED")) {
                jobPostingServiceImpl.updateJobPostingStatus(job.getId(), JobPostingStatus.EXPIRED);


            }
            // Kiểm tra nếu applicationDeadline còn ít hơn hoặc bằng 2 ngày
            long daysLeft = ChronoUnit.DAYS.between(today, deadline);
            if (daysLeft <= 2 && daysLeft > 0) {
                User admin = userServiceImpl.findByUsername("admin01");
                List<JobSaved> listJobSaved = job.getSavedJobs();
                listJobSaved.forEach(eachJobSaved -> {
                    User user = eachJobSaved.getUser();
                    Notification noti = new Notification();
                    noti.setTitle("Reminder: Job Expiring Soon!");
                    noti.setSenderUser(admin);
                    noti.setCreatedAt(LocalDateTime.now());
                    noti.setRecipientUser(user);
                    noti.setType(NotificationType.DEADLINE_REMINDER);
                    noti.setAvatar(admin.getProfilePicture());
                    noti.setRead(false);
                    Notification newNoti = notificationRepository.save(noti);
                    noti.setHref("/jobSeeker/job-save?notiId=" + newNoti.getId());
                    notificationRepository.save(newNoti);
                    notificationService.sendReviewNotificationToUser("Reminder: Job Expiring Soon!", user, admin.getProfilePicture(), admin.getFullName(), newNoti.getHref());
                });


            }
        }

        System.out.println("✅ Complete checking the expired job posting.");
    }
}
