package com.example.baoNgoCv.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct; // Import PostConstruct nếu dùng cách này
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class SmsService {


    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    @Value("${verification.code.expiry.seconds}")
    private int codeExpirySeconds;

    private Random random = new Random();
    // !!! CẢNH BÁO THIẾT KẾ: Cách lưu trữ code này không an toàn cho đa người dùng !!!
    private String verificationCode;
    private LocalDateTime expiryTime;

    // --- SỬA ĐỔI: Bỏ khối static, dùng PostConstruct (hoặc Constructor) ---
    @PostConstruct
    private void initializeTwilio() {
        // Sử dụng các biến instance đã được inject
        Twilio.init(accountSid, authToken);
        System.out.println("Twilio initialized with injected credentials.");
    }

    public String generateVerificationCode() {
        return String.format("%06d", random.nextInt(999999));
    }

    public void sendVerificationCode(String toPhoneNumber, String code) {
        // Đảm bảo số điện thoại có mã quốc gia +84
        if (!toPhoneNumber.startsWith("+")) {
            if(toPhoneNumber.startsWith("0")) {
                toPhoneNumber = "+84" + toPhoneNumber.substring(1);
            } else {
                System.err.println("Invalid phone number format: " + toPhoneNumber);
                return;
            }
        } else if (!toPhoneNumber.startsWith("+84")) {
            System.err.println("Unsupported country code or invalid format: " + toPhoneNumber);
            return;
        }

        try {
            Message message = Message.creator(
                            new PhoneNumber(toPhoneNumber),
                            // --- SỬA ĐỔI: Sử dụng biến đã được inject ---
                            new PhoneNumber(twilioPhoneNumber),
                            "Your verification code is: " + code)
                    .create();

            System.out.println("Message SID: " + message.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send SMS via Twilio: " + e.getMessage());
        }
    }

    // Lưu mã xác thực và thời gian hết hạn
    // !!! CẢNH BÁO THIẾT KẾ: Cách lưu trữ code này không an toàn cho đa người dùng !!!
    public void storeVerificationCode(String code) {
        this.verificationCode = code;
        // --- SỬA ĐỔI: Sử dụng giá trị thời gian hết hạn đã inject ---
        this.expiryTime = LocalDateTime.now().plusSeconds(codeExpirySeconds);
        System.out.println("Stored code: " + code + ", expires at: " + this.expiryTime);
    }

    // !!! CẢNH BÁO THIẾT KẾ: Logic xác thực này cũng bị ảnh hưởng bởi việc lưu trữ sai cách !!!
    public boolean verifyCode(String userInputCode) {
        System.out.println("Verifying input: " + userInputCode + " against stored: " + verificationCode);
        if (verificationCode != null && verificationCode.equals(userInputCode)) {
            if (expiryTime != null && LocalDateTime.now().isBefore(expiryTime)) {
                System.out.println("Code verified successfully.");
                return true;
            } else {
                System.out.println("Code expired. Expiry time was: " + expiryTime);
            }
        } else {
            System.out.println("Code mismatch or no code stored.");
        }
        return false;
    }
}