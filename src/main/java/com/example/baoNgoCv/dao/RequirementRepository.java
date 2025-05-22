package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.JobExperience;
import com.example.baoNgoCv.model.JobPosting;
import com.example.baoNgoCv.model.Requirement;
import com.example.baoNgoCv.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Long> {
    Set<Requirement> findByJobPosting(JobPosting jobPosting);

    @Transactional
    void deleteByJobPosting(JobPosting jobPosting);
}
