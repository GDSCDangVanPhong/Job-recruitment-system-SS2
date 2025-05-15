package com.example.baoNgoCv.dao;

import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.Notification;
import com.example.baoNgoCv.model.Permission;
import com.example.baoNgoCv.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientUser(User user);

    List<Notification> findByRecipientCompany(Company company);
}