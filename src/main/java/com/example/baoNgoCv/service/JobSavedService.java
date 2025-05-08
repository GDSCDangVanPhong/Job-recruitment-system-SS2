package com.example.baoNgoCv.service;

import com.example.baoNgoCv.dao.JobPostingRepository;
import com.example.baoNgoCv.dao.JobSavedRepository;
import com.example.baoNgoCv.model.JobPosting;
import com.example.baoNgoCv.model.JobSaved;
import com.example.baoNgoCv.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSavedService {

    private final JobPostingRepository jobPostingRepository;
    private final UserServiceImpl userServiceImpl;
    private final JobSavedRepository jobSavedRepository;

    public JobSavedService(JobPostingRepository jobPostingRepository, UserServiceImpl userServiceImpl, JobSavedRepository jobSavedRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.userServiceImpl = userServiceImpl;
        this.jobSavedRepository = jobSavedRepository;
    }

    // Kiểm tra công việc đã được lưu cho người dùng
    public boolean isJobSaved(JobPosting jobPosting) {


        User currentUser = userServiceImpl.getCurrentUser();
        if (currentUser == null) {
            return false; // Không tìm thấy người dùng
        }

        // Kiểm tra nếu công việc đã được lưu
        Optional<JobSaved> existingJobSaved = jobSavedRepository.findByJobPostingAndUser(jobPosting, currentUser);
        return existingJobSaved.isPresent(); // Trả về true nếu công việc đã được lưu
    }
}
