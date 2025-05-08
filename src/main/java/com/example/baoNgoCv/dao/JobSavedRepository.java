package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobSavedRepository extends JpaRepository<JobSaved, Long> {


    Optional<JobSaved> findByJobPostingAndUser(JobPosting jobPosting, User currentUser);

    List<JobSaved> findByUser(User user);

    List<JobSaved> findByJobPosting(JobPosting jobPosting);
}