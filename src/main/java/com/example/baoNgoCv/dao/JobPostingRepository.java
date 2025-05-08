package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.JobPosting;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {

    Optional<JobPosting> findById(Long id);

    List<JobPosting> findByIndustryId(Long industryId);

    List<JobPosting> findByCompanyId(Long id);

    // Cập nhật phương thức để trả về Page<JobPosting> thay vì List<JobPosting>
    @Query("SELECT j FROM JobPosting j WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:location IS NULL OR :location = '' OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:experience IS NULL OR :experience = '' OR LOWER(j.experience) LIKE LOWER(CONCAT('%', :experience, '%'))) " +
            "AND (:salaryRange IS NULL OR :salaryRange = '' OR j.salaryRange LIKE CONCAT('%', :salaryRange, '%'))")
    Page<JobPosting> searchJobPostings(@Param("keyword") String keyword,
                                       @Param("location") String location,
                                       @Param("experience") String experience,
                                       @Param("salaryRange") String salaryRange,
                                       Pageable pageable); // Thêm Pageable để hỗ trợ phân trang

    @Transactional
    void deleteById(Long id);
    @Query("SELECT j FROM JobPosting j WHERE j.title LIKE %:keyword%")
    List<JobPosting> findByTitle(@Param("keyword") String keyword);


}
