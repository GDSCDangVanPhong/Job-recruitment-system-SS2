package com.example.baoNgoCv.controller;

import com.example.baoNgoCv.dao.*;
import com.example.baoNgoCv.dto.CompanyInformationUpdateDTO;
import com.example.baoNgoCv.dto.CompanyDTO;
import com.example.baoNgoCv.dto.JobPostingDTO;
import com.example.baoNgoCv.model.*;
import com.example.baoNgoCv.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

@Controller
@RequestMapping("/company")
public class CompanyController {
    private final JobPostingRepository jobPostingRepository;
    private final RequirementRepository requirementRepository;
    private final ApplicantRepository applicantRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    CompanyService companyService;
    UserService userService;
    EmailService emailService;
    PermissionRepository permissionRepository;
    FileService fileService;
    IndustryRepository industryRepository;
    JobPostingService jobPostingService;
    ApplicantService applicantService;
    ApplicationReviewRepository applicationReviewRepository;


    @Autowired
    public CompanyController(PermissionRepository permissionRepository, IndustryRepository industryRepository, FileService fileService, UserService userService, EmailService emailService, CompanyService companyService, JobPostingRepository jobPostingRepository, RequirementRepository requirementRepository, JobPostingService jobPostingService, ApplicantRepository applicantRepository, ApplicantService applicantService, ApplicationReviewRepository applicationReviewRepository, NotificationService notificationService, NotificationRepository notificationRepository, UserRepository userRepository) {
        this.userService = userService;
        this.companyService = companyService;
        this.emailService = emailService;
        this.permissionRepository = permissionRepository;
        this.fileService = fileService;
        this.industryRepository = industryRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.requirementRepository = requirementRepository;
        this.jobPostingService = jobPostingService;
        this.applicantRepository = applicantRepository;
        this.applicantService = applicantService;
        this.applicationReviewRepository = applicationReviewRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/account")
    public String handleCompanyRegistration(@ModelAttribute("company") @Valid CompanyDTO companyDTO,
                                            BindingResult bindingResult,
                                            Model model, RedirectAttributes redirectAttributes) throws MessagingException {

        String companyName = companyDTO.getName();
        String companyEmail = companyDTO.getContactEmail();
        Optional<Company> existingCompany = companyService.findByName(companyName);
        Optional<Company> existingEmail = companyService.findByEmail(companyEmail);
        Optional<Company> existingUsernameCompany = companyService.findByUserName(companyDTO.getUsername());
        User existingUser = userService.findByUsername(companyDTO.getUsername());

        User userExistingEmail = userService.findByEmail(companyEmail);

        if (bindingResult.hasErrors() || existingCompany.isPresent() || existingEmail.isPresent() ||
                !companyDTO.getConfirmPassword().equals(companyDTO.getPassword()) || existingUser != null || userExistingEmail != null) {

            model.addAttribute("company", companyDTO);
            if (existingUser != null || existingUsernameCompany.isPresent()) {
                model.addAttribute("usernameAvai", "Username is already taken, please choose another.");
            }

            if (existingCompany.isPresent()) {
                model.addAttribute("nameAvai", "Name is already taken, please choose another.");
            }
            if (existingEmail.isPresent() || userExistingEmail != null) {
                model.addAttribute("emailAvai", "Email is already taken, please choose another.");
            }
            if (!companyDTO.getConfirmPassword().equals(companyDTO.getPassword())) {
                model.addAttribute("matchingPasswordError", "The password confirmation does not match.");
            }

            return "/company/company-register";
        } else {
            String verificationCode = emailService.generateVerificationCode();
            emailService.sendVerificationCode(companyDTO.getContactEmail(), verificationCode);
            emailService.storeVerificationCode(verificationCode);
            model.addAttribute("resetTimer", true);
            model.addAttribute("companyDTO", companyDTO);


            return "/company/verify-email-company";
        }

    }
    @GetMapping("/test")
    public String test(@ModelAttribute("company") @Valid CompanyDTO companyDTO,
                                            BindingResult bindingResult,
                                            Model model, RedirectAttributes redirectAttributes) throws MessagingException {



            return "/test";


    }

    @GetMapping("/account")
    public String getRegistrationForm(
            Model model) {


        model.addAttribute("company", new CompanyDTO());
        return "company/company-register";
    }

    @GetMapping("/profile")
    public String getProfile(
            Model model) {
        Company currentCompany = userService.getCurrentCompany();

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String createdAtString = "";
        String updatedAtString = "";
        if (currentCompany.getCreatedAt() != null) {
            createdAtString = currentCompany.getCreatedAt().format(dateFormat);
        }
        if (currentCompany.getUpdatedAt() != null) {
            updatedAtString = currentCompany.getUpdatedAt().format(dateFormat);
        }

        String profileImageUrl = fileService.getFileUrl(currentCompany.getCompanyLogo());

        model.addAttribute("profileImageUrl", profileImageUrl);
        model.addAttribute("currentCompany", currentCompany);
        model.addAttribute("updatedAtString", updatedAtString);
        model.addAttribute("createdAtString", createdAtString);
        return "company/company-profile";
    }


    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("name") String name,
                              @RequestParam("location") String location,
                              @RequestParam("verificationCode") String code,
                              @RequestParam("email") String email,
                              Model model) {

        boolean isVerified = emailService.verifyCode(code);

        if (isVerified) {

            Company company = new Company();
            BCryptPasswordEncoder bc = new BCryptPasswordEncoder();

            // Mật khẩu và username
            company.setPassword(bc.encode(password));
            company.setContactEmail(email);
            company.setName(name);
            company.setLocation(location);
            company.setUsername(username);


            // Created time
            LocalDateTime now = LocalDateTime.now();
            company.setCreatedAt(now);

            Set<Permission> permissions = new HashSet<>();
            Permission basicPermission = permissionRepository.findByName("ROLE_COMPANY");
            permissions.add(basicPermission);
            company.setPermissions(permissions);
            company.setCompanyLogo("/img/default/companyDefaultLogo.png");
            // Lưu người dùng vào cơ sở dữ liệu
            companyService.save(company);

            model.addAttribute("successMessage", "Your email has been verified, and your account is now active.");


            model.addAttribute("company", new CompanyDTO());

            return "/company/company-register";  // Chuyển hướng đến trang đăng nhập
        } else {
            CompanyDTO cdt = new CompanyDTO();
            cdt.setUsername(username);
            cdt.setPassword(password);
            cdt.setContactEmail(email);
            cdt.setName(name);
            cdt.setLocation(location);
            model.addAttribute("companyDTO", cdt);

            model.addAttribute("error", "Invalid or expired verification code.");
            return "/company/verify-email-company";


        }


    }

    @GetMapping("/profile-update")
    public String getProfileUpdate(Model model) {
        Company currentCompany = userService.getCurrentCompany();
        String profileImageUrl = fileService.getFileUrl(currentCompany.getCompanyLogo());
        List<Industry> industryList = industryRepository.findAll();

        model.addAttribute("industryList", industryList);
        model.addAttribute("profileImageUrl", profileImageUrl);
        model.addAttribute("currentCompany", currentCompany);

        return "/company/company-profile-update";
    }

    @PostMapping("/company-information-update")
    public ResponseEntity<?> updateCompanyProfile(
            @RequestParam(value = "avatar", required = false) MultipartFile avatar,
            @RequestParam("data") String data
    ) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        CompanyInformationUpdateDTO companyData = objectMapper.readValue(data, CompanyInformationUpdateDTO.class);
        Company currentCompany = userService.getCurrentCompany();

        Optional<Industry> industryFinal = industryRepository.findById(companyData.getIndustryId());

        currentCompany.setCompanySize(companyData.getCompanySize());
        currentCompany.setDescription(companyData.getDescription());
        currentCompany.setIndustry(industryFinal.orElse(null));
        companyService.save(currentCompany);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Profile updated successfully!");

        if (avatar != null && !avatar.isEmpty()) {
            try {
                // Lưu avatar vào thư mục hoặc cơ sở dữ liệu
                String avatarName = fileService.storeFile(avatar);
                String avatarUrl = fileService.getFileUrl(avatarName);


                // Xóa ảnh đại diện cũ nếu tồn tại
                if (currentCompany.getCompanyLogo() != null && !currentCompany.getCompanyLogo().isEmpty()) {
                    fileService.deleteFile(currentCompany.getCompanyLogo());
                }


                currentCompany.setCompanyLogo(avatarUrl);
                List<Notification> listNoti = currentCompany.getNotifications();
                listNoti.forEach(noti -> {
                    noti.setAvatar(avatarUrl);
                    notificationRepository.save(noti);

                });

                currentCompany.setUpdatedAt(LocalDateTime.now());
                companyService.save(currentCompany);

            } catch (Exception e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("message", "Failed to upload avatar.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }


        response.put("message", "Profile updated successfully!");
        response.put("avatarUrl", currentCompany.getCompanyLogo());
        response.put("industryName", currentCompany.getIndustry().getName() != null ? currentCompany.getIndustry().getName() : "");
        response.put("companySize", currentCompany.getCompanySize());
        response.put("description", currentCompany.getDescription());


        return ResponseEntity.ok(response);

    }


    @PostMapping("/contact-information-update")
    public ResponseEntity<Map<String, Object>> updateCompanyContactInformation(
            @RequestBody Map<String, String> data) {  // Nhận dữ liệu dưới dạng Map<String, String>
        try {
            // Xử lý dữ liệu từ Map
            String companyName = data.get("companyName");
            String location = data.get("location");
            String website = data.get("website");

            Company currentCompany = userService.getCurrentCompany();
            currentCompany.setUpdatedAt(LocalDateTime.now());
            currentCompany.setName(companyName);
            currentCompany.setLocation(location);
            currentCompany.setWebsite(website);
            companyService.save(currentCompany);


            // Trả về phản hồi thành công
            Map<String, Object> response = Map.of("message", "Profile updated successfully!", "updatedCompanyName", data.get("companyName"),
                    "updatedlocation", data.get("location"), "updatedWebsite", data.get("website"));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to update profile."));
        }
    }

    @GetMapping("/post-a-job")
    public String postAJob(Model model) {
        List<Industry> industryList = industryRepository.findAll();

        model.addAttribute("industryList", industryList);
        model.addAttribute("company", userService.getCurrentCompany());
        return "/company/post-a-job";
    }

    @GetMapping("/jobposting-managing")
    public String manageJobPosting(Model model) {

        List<JobPosting> jobPostings = jobPostingRepository.findByCompanyId(userService.getCurrentCompany().getId());
        List<Industry> industryList = industryRepository.findAll();


        model.addAttribute("industryList", industryList);
        model.addAttribute("jobPostings", jobPostings);
        return "/company/jobposting-managing";
    }

    @PostMapping("/post-a-job")
    public ResponseEntity<Map<String, Object>> handlePostAJob(@ModelAttribute JobPostingDTO jobPostingDTO) {

        if (jobPostingDTO.getTitle() == null || jobPostingDTO.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Job title is required.");
        }

        if (jobPostingDTO.getTitle().length() > 100) {
            throw new IllegalArgumentException("Job title should not exceed 100 characters.");
        }
        if (jobPostingDTO.getDeadline().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Deadline cannot be in the past.");
        }

        List<String> validJobTypes = Arrays.asList("Full-time", "Part-time", "Contract", "Internship", "Temporary");
        if (!validJobTypes.contains(jobPostingDTO.getJobType())) {
            throw new IllegalArgumentException("Invalid job type.");
        }
        Company currentCompany = userService.getCurrentCompany();
        User admin = userRepository.findByUsername("admin01");
        Optional<Industry> industry = industryRepository.findById(Long.valueOf(jobPostingDTO.getCategory()));
        JobPosting newJobPosting = new JobPosting();
        newJobPosting.setTitle(jobPostingDTO.getTitle());
        newJobPosting.setDescription(jobPostingDTO.getDescription());
        newJobPosting.setJobType(jobPostingDTO.getJobType());
        newJobPosting.setCompany(currentCompany);
        newJobPosting.setLocation(jobPostingDTO.getLocation());
        newJobPosting.setBenefit(jobPostingDTO.getBenefits());
        newJobPosting.setExperience(jobPostingDTO.getExperience());
        newJobPosting.setPostedDate(LocalDate.now());
        newJobPosting.setSalaryRange(jobPostingDTO.getSalary());
        newJobPosting.setUpdatedAt(LocalDate.now());
        newJobPosting.setIndustry(industry.orElse(null));
        newJobPosting.setApplicationDeadline(jobPostingDTO.getDeadline());
        newJobPosting.setStatus(JobPostingStatus.OPEN);
        newJobPosting.setCreatedAt(LocalDate.now());
        newJobPosting.setMaxApplicants(jobPostingDTO.getMaxApplicants());
        JobPosting savedJobPosting = jobPostingRepository.save(newJobPosting);

        Set<User> listUserFollower = currentCompany.getFollowers();
        listUserFollower.forEach(eachUser -> {
            // Giả sử bạn có một phương thức tạo thông báo
            Notification notification = new Notification();
            notification.setTitle("A new job posting is available!");  // Thiết lập người nhận thông báo
            notification.setSenderCompany(currentCompany);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setRecipientUser(eachUser);
            notification.setType(NotificationType.NEW_JOB_POSTING);
            notification.setAvatar(currentCompany.getCompanyLogo());
            notification.setRead(false);
            // Lưu thông báo vào cơ sở dữ liệu
            Notification newNoti = notificationRepository.save(notification);
            newNoti.setHref("/jobseeker/job-detail/" + newJobPosting.getId() + "?notiId=" + newNoti.getId());
            notificationRepository.save(newNoti);
            notificationService.sendReviewNotificationToUser("A new job posting is available!", eachUser, currentCompany.getCompanyLogo(), currentCompany.getName(), newNoti.getHref());
        });

        // Xử lý các yêu cầu (requirements)
        if (jobPostingDTO.getRequirements() != null && !jobPostingDTO.getRequirements().isEmpty()) {
            for (String requirementDescription : jobPostingDTO.getRequirements()) {
                Requirement requirement = new Requirement(savedJobPosting, requirementDescription);
                requirementRepository.save(requirement); // Lưu từng Requirement
            }
        }


        Map<String, Object> response = new HashMap<>();
        response.put("message", "Job posted successfully!");

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-job-posting/{id}")
    public ResponseEntity<Map<String, String>> updateJobPosting(@PathVariable Long id, @RequestBody Map<String, String> formData) {
        // In dữ liệu nhận được ra console
        System.out.println("Data nhận được: " + formData);
        System.out.println("Data nhận được: " + id);
        Map<String, String> response = new HashMap<>();
        Optional<Industry> industry = industryRepository.findById(Long.valueOf(formData.get("category")));
        // Kiểm tra tính hợp lệ của job posting
        boolean isValid = jobPostingService.validateJobPosting(formData, response, id);
        if (!isValid) {
            return ResponseEntity.badRequest().body(response);
        }

        // Lấy đối tượng JobPosting từ cơ sở dữ liệu
        Optional<JobPosting> updatedJobPosting = jobPostingRepository.findById(id);

        if (updatedJobPosting.isPresent()) {
            // Cập nhật các trường của JobPosting
            JobPosting jobPosting = updatedJobPosting.get();
            jobPosting.setId(id);  // Set id từ URL
            jobPosting.setCompany(userService.getCurrentCompany());
            jobPosting.setTitle(formData.get("title"));
            jobPosting.setDescription(formData.get("description"));
            jobPosting.setJobType(formData.get("jobType"));
            jobPosting.setLocation(formData.get("location"));
            jobPosting.setIndustry(industry.orElse(null));
            jobPosting.setBenefit(formData.get("benefits"));
            jobPosting.setExperience(formData.get("experience"));
            jobPosting.setSalaryRange(formData.get("salary"));
            jobPosting.setApplicationDeadline(LocalDate.parse(formData.get("deadline")));
            jobPosting.setStatus(JobPostingStatus.OPEN);
            jobPosting.setUpdatedAt(LocalDate.now());

            // Lưu đối tượng đã cập nhật
            JobPosting savedJobPosting = jobPostingRepository.save(jobPosting);

            // Nếu cần xử lý thêm, làm việc với savedJobPosting ở đây
        } else {
            throw new EntityNotFoundException("JobPosting with id " + id + " not found");
        }


        requirementRepository.deleteByJobPosting(updatedJobPosting.get());
        if (formData.containsKey("requirements")) {
            String requirementsString = (String) formData.get("requirements"); // Lấy requirements dưới dạng String

            // Tách chuỗi thành mảng các yêu cầu
            String[] requirements = requirementsString.split(",");

            for (String requirementDescription : requirements) {
                requirementDescription = requirementDescription.trim(); // Loại bỏ khoảng trắng thừa
                if (!requirementDescription.isEmpty()) { // Kiểm tra nếu yêu cầu không rỗng
                    Requirement requirement = new Requirement(updatedJobPosting.orElse(null), requirementDescription);
                    requirementRepository.save(requirement); // Lưu từng Requirement
                }
            }
        }


        if (updatedJobPosting.isPresent() && updatedJobPosting.get().getId() != null) {
            response.put("status", "success");
            response.put("message", "Job posting updated successfully!");
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Failed to update job posting!");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete-job-posting/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable Long id) {
        // Kiểm tra xem job posting có tồn tại không
        if (jobPostingRepository.existsById(id)) {
            Optional<JobPosting> jobPosting = jobPostingRepository.findById(id);
            applicantRepository.deleteByJobPosting(jobPosting.orElse(null));


            jobPostingRepository.deleteById(id); // Xóa job nếu tồn tại
            return ResponseEntity.ok("Job posting deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("Job posting not found.");
        }
    }

    @PutMapping("/update-job-posting-status/{jobPostingId}/{status}")
    public ResponseEntity<Map<String, Object>> updateJobStatus(
            @PathVariable Long jobPostingId,
            @PathVariable String status) {

        try {
            // Chuyển đổi status từ String sang Enum nếu cần
            JobPostingStatus jobPostingStatus = JobPostingStatus.valueOf(status.toUpperCase());

            // Cập nhật trạng thái công việc
            jobPostingService.updateJobPostingStatus(jobPostingId, jobPostingStatus);

            // Tạo phản hồi JSON
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Job status updated successfully.");
            response.put("status", status.toUpperCase());

            response.put("id", jobPostingId);
            return ResponseEntity.status(HttpStatus.OK).body(response);

        } catch (IllegalArgumentException e) {
            // Trả về lỗi khi status không hợp lệ
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid job status value.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);

        } catch (Exception e) {
            // Trả về lỗi chung
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Failed to update job status.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/job-application-list")
    public String jobApplicationDetail(Model model) {
        // Lấy công ty hiện tại
        Company currentCompany = userService.getCurrentCompany();

        // Lấy danh sách các JobPosting của công ty
        List<JobPosting> jobPostings = jobPostingRepository.findByCompanyId(currentCompany.getId());

        // Tạo danh sách để lưu các ứng viên liên quan
        List<Applicant> applicants = new ArrayList<>();

        // Lặp qua từng JobPosting
        for (JobPosting jobPosting : jobPostings) {
            // Lấy danh sách ứng viên liên kết với JobPosting này
            List<Applicant> jobApplicants = applicantRepository.findByJobPostingId(jobPosting.getId());

            // Thêm vào danh sách tổng hợp
            applicants.addAll(jobApplicants);
        }

        // Đưa danh sách ứng viên vào Model để hiển thị trên view
        model.addAttribute("applicants", applicants);

        return "/company/job-application-list";
    }


    @GetMapping("/job-application-detail/{id}")
    public String jobApplicationDetail(@PathVariable("id") Long id,
                                       @RequestParam(value = "notificationId", required = false) Long notificationId,
                                       Model model) {
        // Lấy thông tin Applicant
        Applicant applicant = applicantRepository.getById(id);
        model.addAttribute("applicant", applicant);

        // Kiểm tra xem có notificationId không, nếu có thì cập nhật trạng thái 'read' cho thông báo
        if (notificationId != null) {
            Notification notification = notificationRepository.findById(notificationId).orElse(null);
            if (notification != null && !notification.isRead()) {
                notification.setRead(true);
                notificationRepository.save(notification);
            }
        }

        return "/company/job-application-detail";
    }

    @PostMapping("/approve/{applicantId}")
    public ResponseEntity<Map<String, Object>> approveApplicant(@PathVariable Long applicantId) {

        Map<String, Object> response = new HashMap<>();
        try {
            // Gọi service để phê duyệt ứng viên
            boolean applicant = applicantService.approveApplicant(applicantId);
            if (applicant) {
                // Trả về Map với thông tin thành công
                response.put("status", "success");
                response.put("message", "Applicant approved successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Applicant not found!");
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            // Trả về Map với thông tin lỗi trong trường hợp có lỗi
            response.put("status", "error");
            response.put("message", "Error approving applicant: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/reject/{applicantId}")
    public ResponseEntity<Map<String, Object>> rejectApplicant(@PathVariable Long applicantId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Lấy ứng viên từ database
            Optional<Applicant> applicant = applicantRepository.findById(applicantId);

            if (applicant.isEmpty()) {
                // Trả về thông báo lỗi nếu không tìm thấy ứng viên
                response.put("status", "error");
                response.put("message", "Applicant not found!");
                return ResponseEntity.status(404).body(response);
            }


            if (applicant.get().getStatus() == ApplicationStatus.SHORTLISTED) {
                JobPosting jobPosting = applicant.get().getJobPosting();
                jobPosting.decrementApplicantCount(); // Giảm số lượng ứng viên
                jobPostingRepository.save(jobPosting);
            }

            // Gọi service để từ chối ứng viên
            boolean applicantRejected = applicantService.rejectApplicant(applicantId);

            if (applicantRejected) {
                response.put("status", "success");
                response.put("message", "Applicant rejected successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Error rejecting applicant.");
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error rejecting applicant: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/send-review/{applicantId}")
    public ResponseEntity<Map<String, Object>> sendReview(
            @PathVariable Long applicantId,
            @RequestBody Map<String, Object> requestBody) {

        // Lấy giá trị của trường "message"
        String reviewMessage = (String) requestBody.get("message");

        // Kiểm tra dữ liệu
        if (reviewMessage == null || reviewMessage.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Review message is required!"
            ));
        }

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xem applicantId có hợp lệ không
        if (!applicantService.existsById(applicantId)) {
            response.put("status", "error");
            response.put("message", "Applicant not found!");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        // Lấy thông tin applicant từ repository
        Optional<Applicant> applicant = applicantRepository.findById(applicantId);

        Company currentCompany = userService.getCurrentCompany();

        ApplicationReview applicanApplicationReview = applicationReviewRepository.findByApplicantId(applicantId);
        ApplicationReview newApplicationReview = new ApplicationReview();
        String message = null;
        if (applicanApplicationReview == null) {
            message = "Your applicant has been received a review!";
            newApplicationReview.setApplicant(applicant.get());
            newApplicationReview.setReviewComments(reviewMessage);
            newApplicationReview.setReviewer(currentCompany);
            newApplicationReview.setReviewDate(LocalDateTime.now());
            newApplicationReview.setApplicant(applicant.orElse(null));
        } else {
            message = "The company has updated your review.!";
            applicanApplicationReview.setReviewComments(reviewMessage);
            newApplicationReview = applicanApplicationReview;
            newApplicationReview.setReviewDate(LocalDateTime.now());
        }


        ApplicationReview latestApplicantReview = applicationReviewRepository.save(newApplicationReview);

        Notification notification = new Notification();
        notification.setMessage(reviewMessage);
        notification.setTitle(message);
        notification.setSenderCompany(currentCompany);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRecipientUser(applicantService.getUserByApplicanId(applicantId));
        notification.setType(NotificationType.APPLICATION_REVIEWED);
        notification.setApplicant(applicant.orElse(null));

        notification.setAvatar(fileService.getFileUrl(currentCompany.getCompanyLogo()));


        notification.setRead(false);

        // Lưu thông báo vào cơ sở dữ liệu để lấy notificationId
        notification = notificationRepository.save(notification);

        // Cập nhật lại href với notificationId sau khi lưu thành công
        notification.setHref("/notification/applicant-review-detail/" + newApplicationReview.getId() + "?notificationId=" + notification.getId());
        notificationRepository.save(notification);  // Lưu lại với href đã cập nhật
        notificationService.sendReviewNotificationToUser(message, applicantService.getUserByApplicanId(applicantId), currentCompany.getCompanyLogo(), currentCompany.getName(), notification.getHref());
        response.put("reviewMessage", latestApplicantReview.getReviewComments());
        response.put("status", "success");
        response.put("message", "Review sent successfully!");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/applicant-viewing/{id}")
    public String applicantViewing(@PathVariable Long id,
                                   Model model) {
        List<Applicant> applicantList = applicantRepository.findByJobPostingId(id);

        JobPosting jobPosting = applicantList.isEmpty() ? jobPostingRepository.findById(id).orElse(null) : applicantList.get(0).getJobPosting();


        model.addAttribute("jobPosting", jobPosting);
        model.addAttribute("applicantList", applicantList);

        return "company/applicant-viewing";
    }

    @GetMapping("/getFollower/{id}")
    public ResponseEntity<Map<String, Object>> getFollower(@PathVariable Long id, Model model) {
        // Lấy công ty hiện tại
        Company currentCompany = userService.getCurrentCompany();

        // Lấy danh sách người theo dõi của công ty
        Set<User> listFollower = currentCompany.getFollowers();

        // Tạo một danh sách chứa thông tin của những người theo dõi, bao gồm id
        List<Map<String, Object>> followersData = new ArrayList<>();
        for (User follower : listFollower) {
            Map<String, Object> followerInfo = new HashMap<>();
            followerInfo.put("id", follower.getId());  // Thêm ID của follower
            followerInfo.put("name", follower.getFullName());
            followerInfo.put("avatarUrl", follower.getProfilePicture());  // Giả sử bạn có một thuộc tính avatarUrl trong User
            followersData.add(followerInfo);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("followers", followersData);

        return ResponseEntity.ok(response);  // Trả về ResponseEntity với mã trạng thái OK (200)
    }
}



