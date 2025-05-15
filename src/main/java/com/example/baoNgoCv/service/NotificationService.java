package com.example.baoNgoCv.service;

import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendReviewNotificationToUser(String title, User user,String avatar,String sender,String href) {
        // Construct the notification payload
        Map<String, Object> payload = new HashMap<>();

        payload.put("message", title);
        payload.put("avatar", avatar);
        payload.put("sender", sender);
        payload.put("href", href);

            messagingTemplate.convertAndSend("/topic/user/"+user.getId(), payload);
    }
    public void sendReviewNotificationToCompany(String title, Company company, String avatar, String sender, String href) {
        // Construct the notification payload
        Map<String, Object> payload = new HashMap<>();

        payload.put("message", title);
        payload.put("avatar", avatar);
        payload.put("sender", sender);
        payload.put("href", href);
        // Send the message to all subscribers of /topic/reviews
        messagingTemplate.convertAndSend("/topic/company/"+company.getId(), payload);
    }




}



