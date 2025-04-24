package com.example.baoNgoCv.model;

import java.time.LocalDateTime;

public class VerificationCode {
    private String code;
    private LocalDateTime expiryTime;

    public VerificationCode(String code, LocalDateTime expiryTime) {
        this.code = code;
        this.expiryTime = expiryTime;
    }

    public VerificationCode() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }
}
