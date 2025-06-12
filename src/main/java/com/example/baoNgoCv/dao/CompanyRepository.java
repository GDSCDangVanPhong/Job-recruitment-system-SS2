package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findById(Long id);

    Optional<Company> findByContactEmail(String contactEmail);

    Optional<Company> findByName(String companyName);


    Optional<Company> findByUsername(String username);
}
