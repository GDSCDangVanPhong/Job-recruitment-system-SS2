package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.Education;
import com.example.baoNgoCv.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {
 Set<Education> findByUser(User user);
 long countByUser(User user);
}
