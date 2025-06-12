package com.example.baoNgoCv.service;



import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.User;

import java.util.Optional;

public interface CompanyService {
    Optional<Company> getById(long id);

    void followCompany(Long companyId, Long userId);
    boolean isFollowedByUser(Long companyId, Long userId);
    void unfollowCompany(Long companyId, Long userId);
    Optional<Company> findByName(String companyName);
    Optional<Company> findByUserName(String username);
    Optional<Company> findByEmail(String companyEmail);




    Company save(Company company);



}
