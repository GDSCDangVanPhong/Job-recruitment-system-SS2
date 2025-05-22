package com.example.baoNgoCv.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.from.address}")
    private String mailFromAddress;

    @Value("${app.mail.recipient.contact}")
    private String contactRecipientEmail;

    @Value("${app.mail.recipient.application}")
    private String applicationRecipientEmail;

    // --- Các phần khác giữ nguyên ---
    private Random random = new Random();
    private String verificationCode;
    private LocalDateTime expiryTime;

    public String generateVerificationCode() {
        return String.format("%06d", random.nextInt(999999));
    }

    public void sendVerificationCode(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart

        helper.setFrom(mailFromAddress); // <-- Sử dụng giá trị inject
        helper.setTo(toEmail);
        helper.setSubject("BaoNgoCV - Email Verification Code");
        helper.setText("Your verification code is: " + code);

        mailSender.send(message);
    }

    // storeVerificationCode và verifyCode giữ nguyên (vẫn còn vấn đề thiết kế đa người dùng)
    public void storeVerificationCode(String code) {
        this.verificationCode = code;
        // Nên inject cả giá trị 60 giây này nếu muốn thay đổi dễ dàng
        this.expiryTime = LocalDateTime.now().plusSeconds(60); // Hoặc dùng giá trị inject
    }

    public boolean verifyCode(String userInputCode) {
        if (verificationCode != null && verificationCode.equals(userInputCode)) {
            if (expiryTime != null && LocalDateTime.now().isBefore(expiryTime)) {
                return true;
            }
        }
        return false;
    }


    public void sendContactEmail(String name, String email, String messageContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // <-- Sử dụng giá trị inject -->
        helper.setTo(contactRecipientEmail);
        helper.setFrom(mailFromAddress); // <-- Sử dụng giá trị inject
        helper.setReplyTo(email);
        helper.setSubject("PiconWebsite - Liên hệ mới từ: " + name);

        String emailText = String.format(" /* ... nội dung email ... */");
        helper.setText(emailText, false);

        System.out.println("Chuẩn bị gửi email LIÊN HỆ tới " + contactRecipientEmail + " từ " + email);
        mailSender.send(message);
        System.out.println("Đã gửi email LIÊN HỆ.");
    }

    public void sendApplicationEmail(String name, String email, String phone, String messageContent,
                                     String cvFileName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // <-- Sử dụng giá trị inject -->
        helper.setTo(applicationRecipientEmail);
        helper.setFrom(mailFromAddress); // <-- Sử dụng giá trị inject
        helper.setReplyTo(email);
        helper.setSubject("PiconWebsite - Hồ sơ ứng tuyển mới từ: " + name);

        StringBuilder emailBody = new StringBuilder();
        // ... xây dựng nội dung email ...
        helper.setText(emailBody.toString(), false);

        System.out.println("Chuẩn bị gửi email ỨNG TUYỂN tới " + applicationRecipientEmail + " từ " + email);
        mailSender.send(message);
        System.out.println("Đã gửi email ỨNG TUYỂN.");
    }
}