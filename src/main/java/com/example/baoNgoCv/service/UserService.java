package com.example.baoNgoCv.service;

import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
     User findByUsername(String username);

     User save(User user);

    User findByEmail(String email);

     User getCurrentUser();

    Company getCurrentCompany();

    Boolean   checkPassword(User user, String currentPassword);
    Boolean  isPhoneNumber(long id);


    boolean isUserProfileComplete(User user);

    void addFollower(Long companyId);

    void removeFollower(Long companyId);
}


