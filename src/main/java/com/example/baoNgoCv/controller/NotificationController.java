package com.example.baoNgoCv.controller;

import com.example.baoNgoCv.dao.ApplicationReviewRepository;
import com.example.baoNgoCv.dao.NotificationRepository;
import com.example.baoNgoCv.dto.NotificationDTO;
import com.example.baoNgoCv.model.*;
import com.example.baoNgoCv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationRepository notificationRepository;
    UserService userService;
    private ApplicationReviewRepository applicationReviewRepository;

    @Autowired
    public NotificationController(UserService userService, NotificationRepository notificationRepository, ApplicationReviewRepository applicationReviewRepository) {
        this.userService = userService;
        this.notificationRepository = notificationRepository;
        this.applicationReviewRepository = applicationReviewRepository;
    }

    @GetMapping("/get-notification")
    public ResponseEntity<Map<String, Object>> getNotification() {
        Map<String, Object> response = new HashMap<>();
        List<NotificationDTO> notificationDTOs = new ArrayList<>();

        User user = userService.getCurrentUser();
        Company company = userService.getCurrentCompany();

        if (user != null) {
            List<Notification> listNoti = notificationRepository.findByRecipientUser(user);
            for (Notification notification : listNoti) {
                NotificationDTO dto = new NotificationDTO();
                dto.setId(notification.getId());


                if (notification.getSenderUser() != null) {
                    if (notification.getSenderUser().getFullName() == null) {
                        dto.setSender(notification.getSenderUser().getUsername());
                    } else {
                        dto.setSender(notification.getSenderUser().getFullName());
                    }
                } else if (notification.getSenderCompany() != null) {
                    dto.setSender(notification.getSenderCompany().getName());
                }


                dto.setRead(notification.isRead());
                dto.setTitle(notification.getTitle());
                dto.setCreatedAt(notification.getCreatedAt());
                dto.setAvatar(notification.getAvatar());
                dto.setHref(notification.getHref());
                notificationDTOs.add(dto);


            }
        } else if (company != null) {
            List<Notification> listNoti = notificationRepository.findByRecipientCompany(company);
            for (Notification notification : listNoti) {
                NotificationDTO dto = new NotificationDTO();
                dto.setId(notification.getId());
                dto.setSender(notification.getSenderUser().getFullName());
                dto.setRead(notification.isRead());
                dto.setTitle(notification.getTitle());
                dto.setCreatedAt(notification.getCreatedAt());
                dto.setAvatar(notification.getAvatar());
                dto.setHref(notification.getHref());
                notificationDTOs.add(dto);
            }
        } else {
            response.put("success", false);
            response.put("error", "User not found.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("success", true);
        response.put("notifications", notificationDTOs);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/applicant-review-detail/{id}")
    public String applicantReviewDetail(@PathVariable("id") Long id, @RequestParam("notificationId") Long notificationId, Model model) {
        // Lấy thông báo từ notificationId (từ request param)
        Notification notification = notificationRepository.findById(notificationId).orElse(null);

        if (notification != null) {
            // Đánh dấu thông báo là đã đọc
            notification.setRead(true);

            // Lưu thông báo đã cập nhật vào cơ sở dữ liệu
            notificationRepository.save(notification);
        }

        // Lấy chi tiết đơn ứng tuyển (applicationReview) từ id trong URL path
        ApplicationReview applicationReview = applicationReviewRepository.findById(id).orElse(null);

        // Thêm thông tin vào model để hiển thị trong view
        model.addAttribute("applicationReview", applicationReview);

        // Trả về view chi tiết thông báo
        return "/notification/applicant-review-detail";
    }

}
