package com.example.baoNgoCv.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.baoNgoCv.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);

}
