package com.example.baoNgoCv.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class FileService {
    private final String uploadDir = Paths.get("uploads").toAbsolutePath().toString();

    // Sửa lại phương thức generateUniqueFileName để chỉ sử dụng timestamp và giữ lại phần mở rộng tệp
    private String generateUniqueFileName(String originalFileName) {
        // Lấy timestamp theo định dạng yyyyMMddHHmmss
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        // Lấy phần mở rộng của tệp
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        // Trả về tên tệp duy nhất kết hợp với timestamp và phần mở rộng
        return timestamp + fileExtension;
    }

    // Phương thức lưu tệp
    public String storeFile(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Tạo tên file duy nhất
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            Path filePath = uploadPath.resolve(fileName);

            // Lưu file vào hệ thống
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Chỉ trả về tên file
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename(), e);
        }
    }

    public String getFileUrl(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "/img/default/defaultProfilePicture.jpg"; // Trả về ảnh mặc định
        }
        if (fileName.startsWith("/uploads/")) {
            return fileName; // Nếu đã chứa /uploads/, trả về nguyên bản
        }
        return "/uploads/" + fileName;
    }

    public void deleteFile(String fileUrl) {
        try {
            // Kiểm tra nếu URL trống hoặc null
            if (fileUrl == null || fileUrl.isEmpty()) {
                throw new IllegalArgumentException("File URL cannot be null or empty");
            }

            // Trích xuất tên tệp từ URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // Tạo đường dẫn đầy đủ đến tệp
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();

            // Xóa tệp nếu tồn tại
            if (Files.deleteIfExists(filePath)) {
                System.out.println("Deleted file: " + filePath);
            } else {
                System.out.println("File not found, could not delete: " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file from URL: " + fileUrl, e);
        }
    }


    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource =  new UrlResource(filePath.toUri());

            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found: " + fileName, e);
        }
    }

}
