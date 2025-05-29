package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.Applicant;

import com.example.baoNgoCv.model.ApplicationStatus;
import com.example.baoNgoCv.model.JobPosting;
import com.example.baoNgoCv.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ApplicantRepository extends JpaRepository<Applicant, Long> {


    List<Applicant> findByUser(User user);
    @Transactional
    void deleteByJobPosting(JobPosting jobPosting);

    List<Applicant> findByJobPostingId(Long id);



    @Query("SELECT a.user FROM Applicant a WHERE a.id = :applicantId")
    User findUserByApplicantId(@Param("applicantId") Long applicantId);

    @Query("SELECT a.jobPosting FROM Applicant a WHERE a.id = :applicantId")
    JobPosting findJobPostingByApplicantId(Long applicantId);

    List<Applicant> findByJobPosting(JobPosting jobPosting);
}