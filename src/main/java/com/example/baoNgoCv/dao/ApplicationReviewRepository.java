package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.Applicant;
import com.example.baoNgoCv.model.ApplicationReview;
import com.example.baoNgoCv.model.JobPosting;
import com.example.baoNgoCv.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ApplicationReviewRepository extends JpaRepository<ApplicationReview, Long> {


    ApplicationReview findByApplicantId(Long applicantId);
}