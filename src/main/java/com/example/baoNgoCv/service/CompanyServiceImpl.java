package com.example.baoNgoCv.service;

import com.example.baoNgoCv.dao.CompanyRepository;
import com.example.baoNgoCv.dao.UserRepository;
import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Optional<Company> getById(long id) {
        return companyRepository.findById(id);
    }

    @Override
    public void followCompany(Long companyId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Company> companyOpt = companyRepository.findById(companyId);

        if (userOpt.isPresent() && companyOpt.isPresent()) {
            User user = userOpt.get();
            Company company = companyOpt.get();

            // Thêm công ty vào danh sách công ty đã theo dõi của người dùng
            user.getFollowedCompanies().add(company);
            // Thêm người dùng vào danh sách người theo dõi của công ty
            company.getFollowers().add(user);

            // Lưu lại thay đổi trong cơ sở dữ liệu
            companyRepository.save(company);
            userRepository.save(user);
        } else {
            // Xử lý khi không tìm thấy người dùng hoặc công ty
            // Bạn có thể thêm thông báo lỗi hoặc ngoại lệ tùy theo yêu cầu
            System.out.println("User or Company not found");
        }
    }

    @Override
    public boolean isFollowedByUser(Long companyId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Company> companyOpt = companyRepository.findById(companyId);
        User user = userOpt.get();
        Company company = companyOpt.get();
        return user.getFollowedCompanies().contains(company);

    }

    @Override
    public void unfollowCompany(Long companyId, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Company> companyOpt = companyRepository.findById(companyId);

        if (userOpt.isPresent() && companyOpt.isPresent()) {
            User user = userOpt.get();
            Company company = companyOpt.get();

            // Xóa công ty khỏi danh sách công ty đã theo dõi của người dùng
            user.getFollowedCompanies().remove(company);
            // Xóa người dùng khỏi danh sách người theo dõi của công ty
            company.getFollowers().remove(user);

            // Lưu lại thay đổi trong cơ sở dữ liệu
            companyRepository.save(company);
            userRepository.save(user);
        }
    }

    @Override
    public Optional<Company> findByName(String companyName) {
        return companyRepository.findByName(companyName);
    }

    @Override
    public Optional<Company> findByUserName(String username) {
        return companyRepository.findByUsername(username);
    }

    @Override
    public Optional<Company> findByEmail(String companyEmail) {
        return companyRepository.findByContactEmail(companyEmail);
    }

    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }


}