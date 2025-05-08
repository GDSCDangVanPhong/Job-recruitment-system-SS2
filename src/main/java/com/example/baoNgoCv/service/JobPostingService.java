package com.example.baoNgoCv.service;

import com.example.baoNgoCv.model.JobPosting;
import com.example.baoNgoCv.model.JobPostingStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JobPostingService {

    List<JobPosting> getAll(int page);

    Optional<JobPosting> getJobPostingById(long id);

    List<JobPosting> getJobPostingByIndustryId(Long industryId);

    List<JobPosting> getByCompanyId(Long id);

    Page<JobPosting> searchJobPostings(String keyword, String location, String experience, String salaryRange, int page);

    boolean validateJobPosting(Map<String, String> formData, Map<String, String> response, long id);


    void updateJobPostingStatus(Long jobPostingId, JobPostingStatus jobPostingStatus);


}
