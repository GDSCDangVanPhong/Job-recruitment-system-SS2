package com.example.baoNgoCv.controller;

import com.example.baoNgoCv.dao.NotificationRepository;
import com.example.baoNgoCv.model.JobPosting;
import com.example.baoNgoCv.model.Notification;
import com.example.baoNgoCv.service.JobPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/main")
public class MainController {

    private final JobPostingService jobPostingService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public MainController(JobPostingService jobPostingService, NotificationRepository notificationRepository) {
        this.jobPostingService = jobPostingService;
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/home")
    public String homePage(Model model) {
        List<JobPosting> jobPostings = jobPostingService.getAll(0);


        model.addAttribute("jobPostings", jobPostings);
        return "main/home";
    }

    @GetMapping("/about")
    public String about() {
        return "main/about";
    }

    @GetMapping("/logout")
    public String logout() {
        return "main/about";
    }

    @GetMapping("/password-changing-success")
    public String passwordChangingSuccess(@RequestParam("idNoti") Long id,Model model) {
        if (id == null) {
            return "status/error";
        }
        Optional<Notification> notifi = notificationRepository.findById(id);
        if (!notifi.isPresent()) {
            return "status/error";
        } else {
            notifi.get().setRead(true);
            notificationRepository.save(notifi.get());
            model.addAttribute("notification", notifi.get());
            return "status/password-changing-success";
        }
    }

}
