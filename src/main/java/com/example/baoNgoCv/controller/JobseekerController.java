package com.example.baoNgoCv.controller;

import com.example.baoNgoCv.dao.*;
import com.example.baoNgoCv.dto.EducationUpdateDTO;

import com.example.baoNgoCv.dto.JobPostingSearchDTO;
import com.example.baoNgoCv.dto.PersonalInforUpdateDTO;
import org.springframework.core.io.Resource;
import com.example.baoNgoCv.model.*;
import com.example.baoNgoCv.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/jobseeker")
public class JobseekerController {
    private final ApplicationReviewRepository applicationReviewRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final JobSavedRepository jobSavedRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobSavedService jobSavedService;
    private final UserRepository userRepository;
    RequirementRepository requirementRepository;
    UserService userService;
    EducationRepository educationRepository;
    JobExperienceRepository jobExperienceRepository;
    FileService fileService;
    JobPostingService jobPostingService;
    CompanyService companyService;

    ApplicantRepository applicantRepository;
    ApplicantService applicantService;

    @Autowired
    public JobseekerController(ApplicantService applicantService, JobExperienceRepository jobExperienceRepository, FileService fileService, UserService userService, EducationRepository educationRepository, CompanyService companyService, JobPostingService jobPostingService, JobPostingRepository jobPostingRepository, RequirementRepository requirementRepository, ApplicantRepository applicantRepository, ApplicationReviewRepository applicationReviewRepository, NotificationService notificationService, NotificationRepository notificationRepository, JobSavedRepository jobSavedRepository, JobSavedService jobSavedService, UserRepository userRepository) {
        this.userService = userService;
        this.educationRepository = educationRepository;
        this.jobExperienceRepository = jobExperienceRepository;
        this.fileService = fileService;
        this.jobPostingService = jobPostingService;
        this.requirementRepository = requirementRepository;
        this.companyService = companyService;

        this.applicantRepository = applicantRepository;
        this.applicantService = applicantService;
        this.applicationReviewRepository = applicationReviewRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.jobSavedRepository = jobSavedRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobSavedService = jobSavedService;
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String homePage(@RequestParam(value = "id", required = false) Long id, Model model) {
        User user;
        boolean view;

        if (id != null && userService.getCurrentCompany() != null) {
            // Kiểm tra nếu user tồn tại trong database
            Optional<User> optionalUser = userRepository.findById(id);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
                view = true;
            } else {
                // Nếu không tìm thấy user, chuyển hướng hoặc thông báo lỗi
                return "/status/error";
            }
        } else {
            user = userService.getCurrentUser();
            view = false;
        }

        // Lấy thông tin bổ sung
        Set<Education> educations = educationRepository.findByUser(user);
        Set<JobExperience> jobExperiences = jobExperienceRepository.findByUser(user);

        System.out.println("profile img: " + user.getProfilePicture());
        model.addAttribute("view", view);
        model.addAttribute("jobExperiences", jobExperiences);
        model.addAttribute("educations", educations);
        model.addAttribute("user", user);

        return "/jobseeker/profile";
    }

    @GetMapping("/profile-update")
    public String profileUpdateAction(@RequestParam(value = "idNoti", required = false) Long idNoti, Model model) {
        User user = userService.getCurrentUser();
        Set<Education> educations = educationRepository.findByUser(user);
        Set<JobExperience> jobExperiences = jobExperienceRepository.findByUser(user);
        int educationCount = educations.size();
        boolean showEducation;


        if (educationCount > 0) {
            showEducation = true;
        } else {
            showEducation = false;
        }

        int jobExCount = jobExperiences.size();
        boolean showJobEx;
        if (jobExCount > 0) {
            showJobEx = true;
        } else {
            showJobEx = false;
        }
        // Tạo DTO cho thông tin cá nhân
        PersonalInforUpdateDTO personalInforUpdateDTO = new PersonalInforUpdateDTO();
        personalInforUpdateDTO.setFullName(user.getFullName());
        personalInforUpdateDTO.setEmail(user.getEmail());
        personalInforUpdateDTO.setPhone(user.getPhoneNumber());
        personalInforUpdateDTO.setLocation(user.getAddress());
        personalInforUpdateDTO.setNationality(user.getNationality());
        personalInforUpdateDTO.setDateOfBirth(user.getDateOfBirth());
        String profileImageUrl = user.getProfilePicture();

        if (idNoti != null) {
            Optional<Notification> optionalNotification = notificationRepository.findById(idNoti);
            if (optionalNotification.isPresent()) {
                Notification notification = optionalNotification.get();
                notification.setRead(true); // Đánh dấu là đã đọc
                notificationRepository.save(notification); // Lưu thay đổi vào cơ sở dữ liệu
            }
        }


        model.addAttribute("profileImageUrl", profileImageUrl);
        model.addAttribute("showJobEx", showJobEx);
        model.addAttribute("showEducation", showEducation);
        model.addAttribute("jobExperiences", jobExperiences);
        model.addAttribute("educations", educations);
        model.addAttribute("personalInforUpdateDTO", personalInforUpdateDTO);

        return "jobseeker/profile-update";
    }


    @PutMapping("/edit-education")
    @ResponseBody
    public ResponseEntity<Map<String, String>> editEducation(@RequestBody Map<String, String> data) {
        System.out.println("Received data from client: " + data);

        String degree = data.get("degree");
        String institution = data.get("institution");
        String startDateStr = data.get("startDate");
        String endDateStr = data.get("endDate");
        String detail = data.get("notes");
        String educationIdStr = data.get("id");

        // Kiểm tra các trường bắt buộc
        if (degree == null || degree.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Degree is required."));
        }
        if (institution == null || institution.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Institution is required."));
        }
        if (startDateStr == null || startDateStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date is required."));
        }
        if (endDateStr == null || endDateStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "End date is required."));
        }
        if (educationIdStr == null || educationIdStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "educationIdStr is required."));
        }

        // Chuyển đổi ngày tháng từ String thành LocalDate
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // Kiểm tra ngày bắt đầu không được lớn hơn ngày kết thúc
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date cannot be after end date."));
        }

        // Kiểm tra xem Education có tồn tại hay không với ID đã gửi
        Optional<Education> optionalEducation = educationRepository.findById(Long.valueOf(educationIdStr));
        if (!optionalEducation.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Education record not found."));
        }

        // Cập nhật thông tin Education
        Education education = optionalEducation.get();
        education.setDegree(degree);
        education.setInstitution(institution);
        education.setStartDate(startDate);
        education.setEndDate(endDate);
        education.setNotes(detail);

        // Lưu thông tin đã cập nhật
        try {
            Education updatedEducation = educationRepository.save(education);
            Long updatedEducationId = updatedEducation.getId();
            // Trả về phản hồi thành công
            return ResponseEntity.ok(Map.of("message", "Education information updated successfully.", "degree", data.get("degree"), "institution", data.get("institution"), "endDate", data.get("endDate"), "notes", data.get("notes"), "id", data.get("id"), "startDate", data.get("startDate")));


        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while updating education information."));
        }
    }


    @DeleteMapping("/education-update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteEducation(@PathVariable Long id) {
        try {

            User currentUser = userService.getCurrentUser();

            // Tìm kiếm thông tin giáo dục
            Optional<Education> education = educationRepository.findById(id);
            // Đếm số lượng thông tin giáo dục còn lại của người dùng hiện tại

            if (education.isPresent() && education.get().getUser().equals(currentUser)) {
                // Xóa thông tin giáo dục nếu hợp lệ
                educationRepository.deleteById(id);
                return ResponseEntity.ok(Map.of("message", "Education information is deleted successfully."));


            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Education not found or you do not have permission to delete it."));

            }
        } catch (NumberFormatException e) {
            // Trả về lỗi nếu không thể ép kiểu String sang Long
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid education ID"));
        }
    }


    @PostMapping("/job-ex-add")
    public String jobExAdd(@ModelAttribute Set<EducationUpdateDTO> educationUpdateDTOs) {


        return "redirect:/jobseeker/profile";
    }


    @GetMapping("/job-detail/{id}")
    public String jobDetail(@PathVariable("id") Long id, @RequestParam(value = "notiId", required = false) Long notiId, Model model) {

        if (notiId != null) {
            notificationRepository.findById(notiId)
                    .ifPresent(noti -> {
                        noti.setRead(true);
                        notificationRepository.save(noti);
                    });
        }
        Optional<JobPosting> jobPostingOptional = jobPostingService.getJobPostingById(id);
        User user = userService.getCurrentUser();
        boolean userCheck = user != null;
        List<Applicant> applicants = new ArrayList<>();
        // Kiểm tra xem jobPosting có tồn tại không
        if (jobPostingOptional.isPresent()) {
            applicants = applicantService.getApplicantByUser(userService.getCurrentUser());

            Applicant applicantMatching = applicants.stream().filter(applicant -> applicant.getJobPosting().getId().equals(jobPostingOptional.get().getId())).findFirst().orElse(new Applicant());


            JobPosting jobPosting = jobPostingOptional.get();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = jobPosting.getPostedDate().format(formatter);

            Set<Requirement> requirements = requirementRepository.findByJobPosting(jobPosting);
            List<JobPosting> jobPostingRelevant = jobPostingService.getJobPostingByIndustryId(jobPosting.getIndustry().getId()).stream().filter(jp -> !jp.getId().equals(jobPosting.getId())).toList();
            boolean isApplied = applicantMatching.getId() != null;


            boolean isSavedJob = jobSavedService.isJobSaved(jobPosting);
            model.addAttribute("isSavedJob", isSavedJob);
            model.addAttribute("isApplied", isApplied);
            model.addAttribute("applicantMatching", applicantMatching);
            model.addAttribute("jobPostingRelevant", jobPostingRelevant);
            model.addAttribute("formattedPostedDate", formattedDate);
            model.addAttribute("requirements", requirements);
            model.addAttribute("jobPosting", jobPosting);
            model.addAttribute("userCheck", userCheck);

            return "/jobseeker/job-detail";
        } else {
            model.addAttribute("errorMessage", "Not found");
        }

        return "/jobseeker/job-detail";
    }

    @GetMapping("/company-detail/{id}")
    public String companyDetail(@PathVariable("id") Long id, Model model) {
        Optional<Company> company = companyService.getById(id);
        if (company.isEmpty()) {
            return "redirect:/error"; // Xử lý khi không tìm thấy công ty
        }

        List<JobPosting> jobPostingByCompany = jobPostingService.getByCompanyId(company.get().getId());

        // Lấy thông tin người dùng
        User user = userService.getCurrentUser(); // Trả về null nếu chưa đăng nhập
        String followStatus;

        if (user == null) {
            followStatus = "login to follow"; // Chưa đăng nhập
        } else {
            Long userId = user.getId();
            boolean userHasFollowed = companyService.isFollowedByUser(company.get().getId(), userId);
            followStatus = userHasFollowed ? "followed" : "not yet";
        }

        // Định dạng ngày tạo công ty
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedCreatedAt = company.get().getCreatedAt().format(formatter);

        // Thêm vào model
        model.addAttribute("followStatus", followStatus);
        model.addAttribute("formattedCreatedAt", formattedCreatedAt);
        model.addAttribute("jobPostingByCompany", jobPostingByCompany);
        model.addAttribute("company", company.get());

        return "/jobseeker/company-detail";
    }


    @PostMapping("/education-update")
    @ResponseBody
    public ResponseEntity<Map<String, String>> updateEducation(@RequestBody Map<String, String> data) {
        User currentUser = userService.getCurrentUser();

        Set<Education> educations = educationRepository.findByUser(currentUser);

        int numberOfEducations = educations.size();
        if (numberOfEducations >= 3) {
            return ResponseEntity.badRequest().body(Map.of("errorLimitation", "You have reached the maximum number of allowed education background!"));
        }

        // Kiểm tra dữ liệu nhận được
        String degree = data.get("degree");
        String institution = data.get("institution");
        String startDateStr = data.get("educationStartDate");
        String endDateStr = data.get("educationEndDate");
        String detail = data.get("educationDetail");

        // Kiểm tra các trường bắt buộc
        if (degree == null || degree.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Degree is required."));
        }
        if (institution == null || institution.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Institution is required."));
        }
        if (startDateStr == null || startDateStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date is required."));
        }
        if (endDateStr == null || endDateStr.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "End date is required."));
        }

        // Chuyển đổi ngày tháng từ String thành LocalDate
        LocalDate startDate = LocalDate.parse(startDateStr);
        LocalDate endDate = LocalDate.parse(endDateStr);

        // Kiểm tra ngày bắt đầu không được lớn hơn ngày kết thúc
        if (startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Start date cannot be after end date."));
        }

        // Ánh xạ từ dữ liệu sang thực thể Education
        Education education = new Education();
        education.setUser(currentUser);
        education.setDegree(degree);
        education.setInstitution(institution);
        education.setStartDate(startDate);
        education.setEndDate(endDate);
        education.setNotes(detail);

        // Lưu thông tin
        try {
            Education savedEducation = educationRepository.save(education);
            Long newEducationId = savedEducation.getId();
            String newDegree = savedEducation.getDegree();
            String newInstitution = savedEducation.getInstitution();
            String newStartDate = String.valueOf(savedEducation.getStartDate());
            String newEndDate = String.valueOf(savedEducation.getEndDate());
            String newNotes = String.valueOf(savedEducation.getNotes());


            // Trả về phản hồi thành công
            return ResponseEntity.ok(Map.of("message", "Education information updated successfully.", "educationId", String.valueOf(newEducationId), "degree", newDegree, "institution", newInstitution, "startDate", newStartDate, "endDate", newEndDate, "notes", newNotes


            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An error occurred while updating education information."));
        }


    }

    @PostMapping("/job-ex-update")
    public ResponseEntity<Map<String, String>> updateJobExperience(@RequestBody JobExperience jobExperience) {
        Map<String, String> response = new HashMap<>();
        User currentUser = userService.getCurrentUser();

        try {

            if (jobExperience.getJobTitle() == null || jobExperience.getCompanyName() == null || jobExperience.getStartDate() == null || jobExperience.getEndDate() == null || jobExperience.getDescription() == null) {
                response.put("error", "All fields are required");
                return ResponseEntity.badRequest().body(response);
            }


            JobExperience jobExperience1 = new JobExperience();
            jobExperience1.setJobTitle(jobExperience.getJobTitle());
            jobExperience1.setCompanyName(jobExperience.getCompanyName());
            jobExperience1.setStartDate(jobExperience.getStartDate());
            jobExperience1.setEndDate(jobExperience.getEndDate());
            jobExperience1.setUser(currentUser);
            jobExperience1.setDescription(jobExperience.getDescription());


            // Lưu công việc (giả sử phương thức này lưu công việc vào DB)
            JobExperience jobExperienceNew = jobExperienceRepository.save(jobExperience1);


            response.put("message", "Work experience added successfully");
            response.put("id", String.valueOf(jobExperienceNew.getId()));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "An error occurred while processing your request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/job-ex-update")
    public ResponseEntity<Map<String, String>> deleteJobEx(@RequestBody Map<String, String> data) {
        Map<String, String> response = new HashMap<>();


        try {
            String id = data.get("id");
            Long idLong = Long.parseLong(id);
            jobExperienceRepository.deleteById(idLong);


            response.put("message", "Work experience deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "An error occurred while processing your request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PutMapping("/job-ex-update/{id}")
    public ResponseEntity<Map<String, String>> updateJobEx(@PathVariable Long id, @RequestBody Map<String, String> data) {
        Map<String, String> response = new HashMap<>();

        try {
            // Kiểm tra xem Job Experience có tồn tại không
            Optional<JobExperience> optionalJobExperience = jobExperienceRepository.findById(id);
            if (optionalJobExperience.isEmpty()) {
                response.put("error", "Job experience not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Lấy thông tin từ Request Body
            String jobTitle = data.get("jobTitle");
            String companyName = data.get("companyName");
            String startDate = data.get("startDate");
            String endDate = data.get("endDate");
            String description = data.get("description");


            // Kiểm tra dữ liệu hợp lệ
            if (jobTitle == null || companyName == null || startDate == null || endDate == null) {
                response.put("error", "Missing required fields");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra startDate phải trước endDate
            if (LocalDate.parse(startDate).isAfter(LocalDate.parse(endDate))) {
                response.put("error", "Start date must be before end date");
                return ResponseEntity.badRequest().body(response);
            }

            // Cập nhật Job Experience
            JobExperience jobExperience = optionalJobExperience.get();
            jobExperience.setJobTitle(jobTitle);
            jobExperience.setCompanyName(companyName);
            jobExperience.setStartDate(LocalDate.parse(startDate));
            jobExperience.setEndDate(LocalDate.parse(endDate));
            jobExperience.setDescription(description);

            jobExperienceRepository.save(jobExperience);

            response.put("message", "Job experience updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "An error occurred while processing your request");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/my-application")
    public String getMyApplication(Model model) {
        User currentUser = userService.getCurrentUser();

        List<Applicant> applicants = applicantService.getApplicantByUser(currentUser);
        model.addAttribute("applicants", applicants);
        return "/jobseeker/my-application";
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        System.out.println("File name received: " + fileName);
        Resource resource = (Resource) fileService.loadFileAsResource(fileName);

        // Trả về ResponseEntity chứa tệp
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
    }

    @PostMapping("/apply-job/{id}")
    public String handleApplicationForm(@PathVariable("id") Long jobPostingId, @RequestParam("cvUpload") MultipartFile cvUpload, @RequestParam("coverLetter") String coverLetter, RedirectAttributes redirectAttributes) {
        Optional<JobPosting> jobPostingOptional = jobPostingService.getJobPostingById(jobPostingId);
        User user = userService.getCurrentUser();
        Applicant matchingApplicant = applicantService.getApplicantByUser(userService.getCurrentUser()).stream().filter(applicant -> applicant.getJobPosting().getId().equals(jobPostingOptional.get().getId())).findFirst().orElse(null);


        if (jobPostingOptional.get().getApplicationDeadline().isBefore(LocalDate.now()) || jobPostingOptional.get().getStatus().name() == "EXPIRED") {
            if (jobPostingOptional.get().getStatus().name() != "EXPIRED") {
                jobPostingService.updateJobPostingStatus(jobPostingId, JobPostingStatus.EXPIRED);
            }
            redirectAttributes.addFlashAttribute("message", "This job posting has expired. You can no longer apply.");
            return "redirect:/jobseeker/apply-job/" + jobPostingId;
        }

        // Kiểm tra thông tin cá nhân của người dùng
        if (!userService.isUserProfileComplete(user)) {
            redirectAttributes.addFlashAttribute("message", "Please update your profile before applying for a job.");
            return "redirect:/jobseeker/apply-job/" + jobPostingId;

        }
        if (matchingApplicant != null) {
            redirectAttributes.addFlashAttribute("message", "You have applied for this job already");
            return "redirect:/jobseeker/apply-job/" + jobPostingId;
        }
        if (jobPostingOptional.get().getApplicantCount() >= jobPostingOptional.get().getMaxApplicants()) {
            redirectAttributes.addFlashAttribute("message", "The number of applicants for this job posting has reached its limit.");
            return "redirect:/jobseeker/apply-job/" + jobPostingId;
        }


        try {

            Applicant newApplicant = applicantService.createApplication(jobPostingId, user, cvUpload, coverLetter);

            Notification notification = new Notification();
            notification.setTitle("A Candidate Has Applied for " + jobPostingOptional.get().getTitle());
            notification.setSenderUser(user);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRecipientCompany(jobPostingOptional.get().getCompany());
            notification.setType(NotificationType.NEW_APPLICATION);
            notification.setApplicant(newApplicant);

            notification.setAvatar(fileService.getFileUrl(user.getProfilePicture()));

            notification.setRead(false);
            notification = notificationRepository.save(notification);

            notification.setHref("/company/job-application-detail/" + newApplicant.getId() + "?notificationId=" + notification.getId());

            notificationRepository.save(notification);

            notificationService.sendReviewNotificationToCompany("A Candidate Has Applied for " + jobPostingOptional.get().getTitle(), jobPostingOptional.get().getCompany(), user.getProfilePicture(), user.getFullName(), "/company/job-application-detail/" + newApplicant.getId());
            redirectAttributes.addFlashAttribute("message", "Application submitted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
            return "redirect:/jobseeker/apply-job/" + jobPostingId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", "Unexpected error: " + e.getMessage());
            return "redirect:/jobseeker/apply-job/" + jobPostingId;
        }

        return "redirect:/jobseeker/apply-job/" + jobPostingId; // Chuyển hướng sau khi thành công
    }

    @GetMapping("/apply-job/{id}")
    public String applyJob(@PathVariable("id") Long id, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {


            Optional<JobPosting> jobPosting = jobPostingService.getJobPostingById(id);
            boolean isSavedJob = jobSavedService.isJobSaved(jobPosting.get());

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());

            Set<Requirement> requirements = requirementRepository.findByJobPosting(jobPosting.get());


            model.addAttribute("requirements", requirements);
            model.addAttribute("user", user);
            model.addAttribute("isSavedJob", isSavedJob);

            model.addAttribute("jobPosting", jobPosting.get());
        }


        return "/jobseeker/apply-job";
    }

    @PostMapping("/personal-infor-update")
    public String updateProfile(@ModelAttribute PersonalInforUpdateDTO personalInforUpdateDTO, @RequestParam(value = "avatar", required = false) MultipartFile avatarFile, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "/jobseeker/profile"; // Nếu có lỗi, quay lại trang profile và hiển thị lỗi
        }


        User user = userService.getCurrentUser();


        if (personalInforUpdateDTO.getDateOfBirth() == null) {
            personalInforUpdateDTO.setDateOfBirth(user.getDateOfBirth());
        }


        boolean isProfileUpdated = false;

        // Cập nhật các thông tin khác của người dùng nếu có thay đổi
        if (!personalInforUpdateDTO.getFullName().equals(user.getFullName())) {
            user.setFullName(personalInforUpdateDTO.getFullName());
            isProfileUpdated = true;
        }
        if (!personalInforUpdateDTO.getPhone().equals(user.getPhoneNumber())) {
            user.setPhoneNumber(personalInforUpdateDTO.getPhone());
            isProfileUpdated = true;
        }
        if (!personalInforUpdateDTO.getLocation().equals(user.getAddress())) {
            user.setAddress(personalInforUpdateDTO.getLocation());
            isProfileUpdated = true;
        }
        if (!personalInforUpdateDTO.getGender().equals(user.getGender())) {
            user.setGender(personalInforUpdateDTO.getGender());
            isProfileUpdated = true;
        }
        if (!personalInforUpdateDTO.getDateOfBirth().equals(user.getDateOfBirth())) {
            user.setDateOfBirth(personalInforUpdateDTO.getDateOfBirth());
            isProfileUpdated = true;
        }
        if (!personalInforUpdateDTO.getNationality().equals(user.getNationality())) {
            user.setNationality(personalInforUpdateDTO.getNationality());
            isProfileUpdated = true;
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            // Lấy URL của ảnh đại diện cũ
            String oldAvatarUrl = user.getProfilePicture();

            // Lưu ảnh đại diện mới
            String avatarFileName = fileService.storeFile(avatarFile);
            String avatarUrl = fileService.getFileUrl(avatarFileName);

            // Cập nhật URL ảnh đại diện mới
            user.setProfilePicture(avatarUrl);
            Set<Notification> listNoti = user.getNotifications();
            // Xóa ảnh đại diện cũ nếu tồn tại
            if (oldAvatarUrl != null && !oldAvatarUrl.isEmpty()) {
                fileService.deleteFile(oldAvatarUrl); // Cần đảm bảo phương thức deleteFileByUrl được triển khai
            }

            isProfileUpdated = true;
        }
        // Nếu có thay đổi, lưu lại thông tin người dùng
        if (isProfileUpdated) {
            userService.save(user);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "No changes were made to your profile.");
        }

        return "redirect:/jobseeker/profile";
    }


    @PostMapping("/save-job/{jobPostingId}")
    public ResponseEntity<Map<String, Object>> saveJob(@PathVariable Long jobPostingId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Tìm kiếm JobPosting theo jobPostingId
            JobPosting jobPosting = jobPostingRepository.findById(jobPostingId).orElse(null);

            if (jobPosting == null) {
                response.put("success", false);
                response.put("message", "Job posting not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }


            User currentUser = userService.getCurrentUser();

            // Kiểm tra nếu công việc đã được lưu cho người dùng này
            boolean isJobSaved = jobSavedService.isJobSaved(jobPosting);
            if (isJobSaved) {
                response.put("success", false);
                response.put("message", "You have already saved this job.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Tạo đối tượng JobSaved mới và lưu
            JobSaved jobSaved = new JobSaved();
            jobSaved.setJobPosting(jobPosting);
            jobSaved.setUser(currentUser);
            jobSaved.setSavedAt(LocalDateTime.now());
            jobSavedRepository.save(jobSaved);

            response.put("success", true);
            response.put("message", "Job saved successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/unsave-job/{jobPostingId}")
    public ResponseEntity<Map<String, Object>> unsaveJob(@PathVariable Long jobPostingId) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Tìm kiếm JobPosting theo jobPostingId
            JobPosting jobPosting = jobPostingRepository.findById(jobPostingId).orElse(null);

            if (jobPosting == null) {
                response.put("success", false);
                response.put("message", "Job posting not found.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Lấy người dùng hiện tại
            User currentUser = userService.getCurrentUser();

            // Tìm kiếm công việc đã lưu dựa trên jobPosting và người dùng
            Optional<JobSaved> jobSavedOptional = jobSavedRepository.findByJobPostingAndUser(jobPosting, currentUser);

            if (jobSavedOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Job not saved by the user.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Xóa công việc đã lưu
            jobSavedRepository.delete(jobSavedOptional.get());

            response.put("success", true);
            response.put("message", "Job unsaved successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/job-save")
    public String jobSaved(@RequestParam(value = "notiId", required = false) Long notiId, Model model) {
        if (notiId != null) {
            (notificationRepository.findById(notiId).get()).setRead(true);
            notificationRepository.save((notificationRepository.findById(notiId).get()));
        }
        User user = userService.getCurrentUser();
        List<JobSaved> jobSavedList = jobSavedRepository.findByUser(user);

        // Tạo một danh sách để chứa các công việc đã lưu
        List<JobPosting> jobPostings = new ArrayList<>();

        // Lặp qua danh sách JobSaved để lấy thông tin JobPosting
        for (JobSaved jobSaved : jobSavedList) {
            jobPostings.add(jobSaved.getJobPosting()); // Giả sử JobSaved có phương thức getJobPosting()
        }

        // Truyền danh sách công việc đã lưu vào model
        model.addAttribute("jobPostings", jobPostings);

        return "/jobseeker/job-save";
    }


    @GetMapping("/job-following")
    public ResponseEntity<Map<String, String>> followCompany(@RequestParam Long companyId, @RequestParam boolean follow) {
        Map<String, String> response = new HashMap<>();
        try {
            if (follow) {
                userService.addFollower(companyId);
                response.put("message", "Company followed successfully");
            } else {
                userService.removeFollower(companyId);
                response.put("message", "Company unfollowed successfully");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @DeleteMapping("/delete-applicant/{id}")
    public ResponseEntity<Map<String, String>> deleteApplicant(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            if (id != null) {
                // Kiểm tra xem ứng viên có tồn tại trước khi xóa
                Optional<Applicant> applicant = applicantRepository.findById(id);
                if (applicant.isPresent()) {
                    String resume = applicant.get().getResume();
                    fileService.deleteFile(resume);
                    applicantRepository.deleteById(id);
                    response.put("message", "Deleted successfully!");
                } else {
                    response.put("error", "Applicant not found!");
                }
            } else {
                response.put("error", "Invalid applicant ID!");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/load-more-job-search")
    public ResponseEntity<Map<String, Object>> searchJobPostingsJson(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, @RequestParam(value = "location", required = false, defaultValue = "") String location, @RequestParam(value = "experience", required = false, defaultValue = "") String experience, @RequestParam(value = "salaryRange", required = false, defaultValue = "") String salaryRange, @RequestParam(value = "page", defaultValue = "0") int page) {

        Page<JobPosting> jobResults = jobPostingService.searchJobPostings(keyword, location, experience, salaryRange, page);


        int totalJobs = (int) jobResults.getTotalElements();

        int remainingJobs = totalJobs - ((page + 1) * jobResults.getSize());

        List<JobPostingSearchDTO> jobPostingDTOS = jobResults.getContent().stream().map(jobPosting -> new JobPostingSearchDTO(jobPosting.getId(), jobPosting.getTitle(), jobPosting.getCompany().getName(), jobPosting.getLocation(), jobPosting.getExperience(), jobPosting.getSalaryRange())).collect(Collectors.toList());

        // Tạo response
        Map<String, Object> response = new HashMap<>();
        response.put("jobPostings", jobPostingDTOS); // Danh sách công việc dưới dạng DTO
        response.put("totalPages", jobResults.getTotalPages()); // Tổng số trang
        response.put("remainingJobs", remainingJobs); // Số công việc còn lại

        return ResponseEntity.ok(response);
    }

    @GetMapping("/job-search")
    public String searchJobs(@RequestParam(value = "keyword", required = false, defaultValue = "") String keyword, @RequestParam(value = "location", required = false, defaultValue = "") String location, @RequestParam(value = "experience", required = false, defaultValue = "") String experience, @RequestParam(value = "salaryRange", required = false, defaultValue = "") String salaryRange, @RequestParam(value = "page", defaultValue = "0") int page, // Page mặc định là 0
                             Model model) {

        // Gọi service để tìm kiếm công việc với các tham số
        Page<JobPosting> jobResults = jobPostingService.searchJobPostings(keyword, location, experience, salaryRange, page);

        List<JobPosting> allJobs = new ArrayList<>();
        int pageCount = 0;
        Page<JobPosting> jobPage;
        do {
            jobPage = jobPostingService.searchJobPostings(keyword, location, experience, salaryRange, page);
            allJobs.addAll(jobPage.getContent());
            pageCount++;
        } while (pageCount < jobPage.getTotalPages());

        int remainingJobs = allJobs.size() - jobResults.getContent().size(); // Tổng công việc - số công việc đã tải lên

        if (jobResults.isEmpty()) {
            model.addAttribute("errorMessage", "No jobs found!");
        } else {
            model.addAttribute("jobPostings", jobResults.getContent()); // Lấy danh sách công việc từ Page
            model.addAttribute("totalPages", jobResults.getTotalPages()); // Tổng số trang
            model.addAttribute("currentPage", jobResults.getNumber());  // Trang hiện tại
        }

        // Thêm các thông tin tìm kiếm vào model
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("experience", experience);
        model.addAttribute("salaryRange", salaryRange);
        model.addAttribute("remainingJobs", remainingJobs);  // Truyền remaining jobs vào model

        return "/jobseeker/job-search"; // Trả về view
    }

    @GetMapping("/job-search-live")
    public ResponseEntity<Map<String, Object>> searchJobsLive(
            @RequestParam(value = "keyword") String keyword) {

        Map<String, Object> response = new HashMap<>();

        // Tìm kiếm danh sách công việc theo từ khóa
        List<JobPosting> listJobPosting = jobPostingRepository.findByTitle(keyword);

        // Chuyển đổi danh sách JobPosting thành danh sách JobPostingSearchDTO
        List<JobPostingSearchDTO> jobDTOs = listJobPosting.stream()
                .map(job -> new JobPostingSearchDTO(
                        job.getId(),
                        job.getTitle(),
                        job.getCompany().getName(),  // Giả sử job có thuộc tính company
                        job.getLocation(),
                        job.getExperience(),
                        job.getSalaryRange()
                ))
                .collect(Collectors.toList());

        // Thêm dữ liệu vào response
        response.put("listRelevant", jobDTOs);


        return ResponseEntity.ok(response);
    }
}

