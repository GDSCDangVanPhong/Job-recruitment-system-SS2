package com.example.baoNgoCv.controller;

import com.example.baoNgoCv.dao.NotificationRepository;
import com.example.baoNgoCv.dao.PermissionRepository;
import com.example.baoNgoCv.dto.CompanyDTO;
import com.example.baoNgoCv.dto.UserRegistrationDTO;
import com.example.baoNgoCv.model.*;
import com.example.baoNgoCv.service.*;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/user")
public class UserController {
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationRepository notificationRepository;
    private final FileService fileService;
    private final NotificationService notificationService;
    UserService userService;
    EmailService emailService;
    CompanyService companyService;
    SmsService smsService;

    @Autowired
    public UserController(CompanyService companyService, UserService userService, PermissionRepository permissionRepository, EmailService emailService, PasswordEncoder passwordEncoder, NotificationRepository notificationRepository, FileService fileService, NotificationService notificationService, SmsService smsService) {
        this.userService = userService;
        this.permissionRepository = permissionRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.companyService = companyService;
        this.notificationRepository = notificationRepository;
        this.fileService = fileService;
        this.notificationService = notificationService;
        this.smsService = smsService;
    }


    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @GetMapping("/register")
    public String register(Model model) {

        model.addAttribute("companyDTO", new CompanyDTO());
        model.addAttribute("userRegistrationDTO", new UserRegistrationDTO());
        return "/user/register";
    }

    @GetMapping("/forget-password")
    public String forgetPassword(Model model) {
        User user = userService.getCurrentUser();
        Company company = userService.getCurrentCompany(); // Sửa lỗi chính tả

        if (user != null) model.addAttribute("user", user);
        if (company != null) model.addAttribute("company", company); // Loại bỏ dấu `;` thừa

        return "/user/forget-password";
    }


    @InitBinder
    public void initBinder(WebDataBinder data) {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);
        data.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    @PostMapping("/register")
    public String registerAction(@Valid @ModelAttribute("userRegistrationDTO") UserRegistrationDTO userRegistrationDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 HttpSession session) throws MessagingException {
        String username = userRegistrationDTO.getUsername();
        String email = userRegistrationDTO.getEmail();
        User existingUser = userService.findByUsername(username);
        User existingEmail = userService.findByEmail(email);
        Optional<Company> existingCompany = companyService.findByUserName(username);
        if (bindingResult.hasErrors() || existingUser != null || existingEmail != null ||
                !userRegistrationDTO.getConfirmPassword().equals(userRegistrationDTO.getPassword())) {

            model.addAttribute("userRegistrationDTO", userRegistrationDTO);

            if (existingUser != null || existingCompany.isPresent()) {
                model.addAttribute("usernameAvai", "Username is already taken, please choose another.");
            }
            if (existingEmail != null) {
                model.addAttribute("emailAvai", "Email is already taken, please choose another.");
            }
            if (!userRegistrationDTO.getConfirmPassword().equals(userRegistrationDTO.getPassword())) {
                model.addAttribute("matchingPasswordError", "The password confirmation does not match.");
            }

            return "/user/register";
        } else {

            // Sinh mã xác thực và gửi email
            String verificationCode = emailService.generateVerificationCode();
            emailService.sendVerificationCode(userRegistrationDTO.getEmail(), verificationCode);

            // Lưu mã xác thực và thời gian hết hạn vào session
            emailService.storeVerificationCode(verificationCode);
            model.addAttribute("resetTimer", true);
            // Lưu thông tin đăng ký vào session trước khi chuyển hướng
            model.addAttribute("userRegistrationDTO", userRegistrationDTO);

            return "/user/verify-email";
        }

    }

    // Xử lý xác minh mã email
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam("username") String username,
                              @RequestParam("password") String password,
                              @RequestParam("verificationCode") String code,
                              @RequestParam("email") String email,
                              Model model) {

        boolean isVerified = emailService.verifyCode(code);

        if (isVerified) {

            User user = new User();
            BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
            // Mật khẩu và username
            user.setPassword(bc.encode(password));
            user.setEmail(email);
            user.setUsername(username);

            // Quyền
            Set<Permission> permissions = new HashSet<>();
            Permission basicPermission = permissionRepository.findByName("ROLE_USER");
            permissions.add(basicPermission);
            user.setPermissions(permissions);
            user.setProfilePicture(fileService.getFileUrl(null));
            // Created time
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);

            User admin = userService.findByUsername("admin01");

            // Lưu người dùng vào cơ sở dữ liệu
            User newUser = userService.save(user);


            Notification newNoti = new Notification();

            newNoti.setTitle("Complete Your Profile to Start Applying for Jobs!");
            newNoti.setSenderUser(admin);
            newNoti.setCreatedAt(LocalDateTime.now());
            newNoti.setRecipientUser(newUser);
            newNoti.setType(NotificationType.BACKGROUND_CHECK);
            newNoti.setAvatar(admin.getProfilePicture());
            newNoti.setRead(false);
            Notification newEstNoti = notificationRepository.save(newNoti);
            newEstNoti.setHref("/jobseeker/profile-update?idNoti=" + newNoti.getId());
            notificationRepository.save(newNoti);
            model.addAttribute("success", "Your email has been verified, and your account is now active.");

            UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();

            model.addAttribute("userRegistrationDTO", userRegistrationDTO);

            return "/user/register";  // Chuyển hướng đến trang đăng nhập
        } else {
            UserRegistrationDTO userRegistrationDTO = new UserRegistrationDTO();
            userRegistrationDTO.setUsername(username);
            userRegistrationDTO.setPassword(password);
            userRegistrationDTO.setEmail(email);
            model.addAttribute("userRegistrationDTO", userRegistrationDTO);

            model.addAttribute("error", "Invalid or expired verification code.");
            return "/user/verify-email";


        }


    }

    @GetMapping("/password-changing")
    public String passwordChanging() {


        return "/user/password-changing";
    }

    @PostMapping("/password-changing")
    public String handlePasswordChanging(
            @RequestParam(value = "currentPassword") String currentPassword,

            @RequestParam("newPassword") String newPassword,
            @RequestParam(value = "confirmedNewPassword", required = false) String confirmedNewPassword,
            Model model) {


        User currentUser = userService.getCurrentUser();


        if (!userService.checkPassword(currentUser, currentPassword) || !newPassword.equals(confirmedNewPassword) || newPassword.length() < 6) {
            if (!userService.checkPassword(currentUser, currentPassword)) {
                model.addAttribute("currentPasswordError", "Current password is incorrect.");
            }

            if (!newPassword.equals(confirmedNewPassword)) {
                model.addAttribute("matchingError", "New password and confirmation do not match.");

            }
            if (newPassword.length() < 6) {
                model.addAttribute("lengthError", "New password must be at least 6 characters long.");

            }

            return "/user/password-changing";
        } else {
            boolean isPhoneNumber = userService.isPhoneNumber(currentUser.getId());
            model.addAttribute("isPhoneNumber", isPhoneNumber);
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("phoneNumber", currentUser.getPhoneNumber());
            return "/user/password-changing-final";
        }


    }


    @GetMapping("/password-changing-final")
    public String passwordChangingFinal() {


        return "/user/password-changing-final";


    }

    @PostMapping("/password-changing-final-email")
    public String handlePasswordChangingFinalEmail(@RequestParam("emailVerificationCode") String code, @RequestParam("newPassword") String newPassword, Model model) {
        boolean isVerified = emailService.verifyCode(code);
        User currentUser = userService.getCurrentUser();
        if (isVerified) {
            User user = userService.getCurrentUser();
            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);

            User admin = userService.findByUsername("admin01");
            userService.save(user);
            model.addAttribute("success", "you have change password successfully!");
            Notification noti = new Notification();
            noti.setTitle("You have change your password successfully!");
            noti.setSenderUser(admin);
            noti.setCreatedAt(LocalDateTime.now());
            noti.setRecipientUser(currentUser);
            noti.setType(NotificationType.PASSWORD_CHANGED);
            noti.setAvatar(admin.getProfilePicture());
            noti.setRead(false);
            Notification estNoti = notificationRepository.save(noti);
            estNoti.setHref("/main/password-changing-success?idNoti=" + estNoti.getId());
            notificationRepository.save(estNoti);
            notificationService.sendReviewNotificationToUser("You have change your password successfully!", user, admin.getProfilePicture(), admin.getFullName(), estNoti.getHref());
            return "/user/password-changing";
        } else {
            boolean isPhoneNumber = userService.isPhoneNumber(currentUser.getId());
            model.addAttribute("isPhoneNumber", isPhoneNumber);
            model.addAttribute("newPassword", newPassword);

            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("error", "Invalid or expired verification code.");
            model.addAttribute("phoneNumber", currentUser.getPhoneNumber());
            return "/user/password-changing-final";


        }


    }

    @PostMapping("/password-changing-final-phone")
    public String handlePasswordChangingFinalPhone(@RequestParam("phoneVerificationCode") String code, @RequestParam("newPassword") String newPassword, Model model) {
        boolean isVerified = smsService.verifyCode(code);
        User currentUser = userService.getCurrentUser();
        if (isVerified) {
            User user = userService.getCurrentUser();
            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);

            User admin = userService.findByUsername("admin01");
            userService.save(user);
            model.addAttribute("success", "you have change password successfully!");
            Notification noti = new Notification();
            noti.setTitle("You have change your password successfully!");
            noti.setSenderUser(admin);
            noti.setCreatedAt(LocalDateTime.now());
            noti.setRecipientUser(currentUser);
            noti.setType(NotificationType.PASSWORD_CHANGED);
            noti.setAvatar(admin.getProfilePicture());
            noti.setRead(false);
            Notification estNoti = notificationRepository.save(noti);
            estNoti.setHref("/main/password-changing-success?idNoti=" + estNoti.getId());
            notificationRepository.save(estNoti);
            notificationService.sendReviewNotificationToUser("You have change your password successfully!", user, admin.getProfilePicture(), admin.getFullName(), estNoti.getHref());
            return "/user/password-changing";
        } else {
            boolean isPhoneNumber = userService.isPhoneNumber(currentUser.getId());
            model.addAttribute("isPhoneNumber", isPhoneNumber);
            model.addAttribute("newPassword", newPassword);

            model.addAttribute("email", currentUser.getEmail());
            model.addAttribute("error", "Invalid or expired verification code.");
            model.addAttribute("phoneNumber", currentUser.getPhoneNumber());
            return "/user/password-changing-final";


        }


    }

    @GetMapping("/forget-password-final")
    public String redirectPasswordForgetFinal(
            @RequestParam("username") String username,
            Model model) {
        model.addAttribute("newPassword", "");

        model.addAttribute("username", username);

        return "/user/forget-password-final";


    }

    @PostMapping("/forget-password-final")
    public String handlePasswordForgetFinal(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("username") String username,
            Model model) {


        User user = userService.findByUsername(username);
        if (newPassword.length() < 6) {
            model.addAttribute("newPassword", newPassword);
            model.addAttribute("error", "new password length must be at least 6 characters");
            model.addAttribute("username", username);
            return "/user/forget-password-final";
        } else {
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.save(user);

            model.addAttribute("success", "You have change your password successfully!");

            return "/user/forget-password-final";
        }


    }

    @PostMapping("/password-forget-final-email")
    public ResponseEntity<Map<String, Object>> handlePasswordForgetFinalEmail(@RequestBody Map<String, String> payload) {
        Map<String, Object> response = new HashMap<>();

        String code = payload.get("emailVerificationCode");
        String username = payload.get("username");

        if (code == null || username == null) {
            response.put("error", "Lack of needed information.");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.findByUsername(username);
        if (user == null) {
            response.put("error", "The user is not existed.");
            return ResponseEntity.badRequest().body(response);
        }

        boolean isVerified = emailService.verifyCode(code);

        if (isVerified) {
            response.put("message", "Verified successfully! You can continue.");
            response.put("status", "success");
            response.put("redirectUrl", "/user/forget-password-final?username=" + username);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "The code is not valid or expired");
            response.put("status", "failed");

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping("/send-email-code-forget-password/{currentUsername}")
    public ResponseEntity<Map<String, Object>> sendEmailCodeForgetPassword(@PathVariable("currentUsername") String currentUsername) throws MessagingException {
        System.out.println("Đã nhận được endpoint");
        Map<String, Object> response = new HashMap<>();
        if (currentUsername == null || currentUsername.isEmpty()) {
            response.put("success", false);
            response.put("message", "Username is required.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        User user = userService.findByUsername(currentUsername);
        // Kiểm tra người dùng
        if (user == null) {
            response.put("success", false);
            response.put("message", "An error happen");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        // Sinh mã xác thực
        String verificationCode = emailService.generateVerificationCode();
        // Gửi email và lưu mã xác thực
        emailService.sendVerificationCode(user.getEmail(), verificationCode);
        emailService.storeVerificationCode(verificationCode);

        // Trả về thông tin thành công
        response.put("success", true);
        response.put("message", "A verification code has been sent to your email.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/send-email-code")
    public ResponseEntity<Map<String, Object>> sendEmailCode() throws MessagingException {
        Map<String, Object> response = new HashMap<>();

        User user = userService.getCurrentUser();
        // Kiểm tra người dùng
        if (user == null) {
            response.put("success", false);
            response.put("message", "An error happen");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        // Sinh mã xác thực
        String verificationCode = emailService.generateVerificationCode();
        // Gửi email và lưu mã xác thực
        emailService.sendVerificationCode(user.getEmail(), verificationCode);
        emailService.storeVerificationCode(verificationCode);

        // Trả về thông tin thành công
        response.put("success", true);
        response.put("message", "A verification code has been sent to your email.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/send-phone-code")
    public ResponseEntity<Map<String, Object>> sendPhoneCode() throws MessagingException {
        Map<String, Object> response = new HashMap<>();

        // Sinh mã xác thực
        String verificationCode = smsService.generateVerificationCode();
        User user = userService.getCurrentUser();

        // Kiểm tra người dùng
        if (user == null) {
            response.put("success", false);
            response.put("message", "User not found, please login first.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Gửi email và lưu mã xác thực
        smsService.sendVerificationCode(user.getPhoneNumber(), verificationCode);
        smsService.storeVerificationCode(verificationCode);

        // Trả về thông tin thành công
        response.put("success", true);
        response.put("message", "A verification code has been sent to your email.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-current-user")
    public ResponseEntity<Map<String, Object>> getUser() {
        Map<String, Object> response = new HashMap<>();

        User user = userService.getCurrentUser();
        Company company = userService.getCurrentCompany();

        // Kiểm tra người dùng
        if (user != null) {
            response.put("success", true);
            response.put("id", user.getId());
            response.put("userType", "user");
            return ResponseEntity.ok(response);
        } else if (company != null) {
            response.put("success", true);
            response.put("id", company.getId());
            response.put("userType", "company");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "User not found, please login first.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsername(@RequestParam("username") String username) {
        Map<String, Object> response = new HashMap<>();
        User user = userService.findByUsername(username);

        if (user == null) {
            response.put("error", "The user does not exist");
            return ResponseEntity.ok(response);
        }

        response.put("phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        response.put("email", user.getEmail());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public String checkUsername() {



        return "test";
    }

}
