package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.JobExperience;
import com.example.baoNgoCv.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface JobExperienceRepository extends JpaRepository<JobExperience, Long> {
 Set<JobExperience> findByUser(User user);

}
