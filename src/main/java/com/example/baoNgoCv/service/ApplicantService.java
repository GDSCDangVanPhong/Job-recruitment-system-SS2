package com.example.baoNgoCv.service;

import com.example.baoNgoCv.model.Applicant;
import com.example.baoNgoCv.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ApplicantService {

    // Tạo đơn ứng tuyển mới
    Applicant createApplication(Long jobPostingId, User user, MultipartFile cvUpload, String coverLetter);

    List<Applicant> getApplicantByUser(User user);

    Boolean approveApplicant(Long applicantId);

    Boolean rejectApplicant(Long applicantId);

    boolean existsById(Long applicantId);
    User getUserByApplicanId(Long applicantId);

}