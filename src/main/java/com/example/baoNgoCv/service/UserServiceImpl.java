package com.example.baoNgoCv.service;

import com.example.baoNgoCv.dao.CompanyRepository;
import com.example.baoNgoCv.dao.UserRepository;
import com.example.baoNgoCv.model.Company;
import com.example.baoNgoCv.model.Permission;
import com.example.baoNgoCv.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CompanyRepository companyRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyRepository = companyRepository;
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.findByUsername(userDetails.getUsername());

        }
        return null;
    }

    @Override
    public Company getCurrentCompany() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Optional<Company> companyOptional = companyRepository.findByUsername(userDetails.getUsername());

            if (companyOptional.isPresent()) {
                return companyOptional.get(); // Trả về giá trị nếu có
            }
        }
        return null; // Trả về null nếu không có công ty
    }


    @Override
    public Boolean checkPassword(User user, String currentPassword) {

        return passwordEncoder.matches(currentPassword, user.getPassword());


    }

    @Override
    public Boolean isPhoneNumber(long id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {

            User foundUser = user.get();


            if (foundUser.getPhoneNumber() != null && !foundUser.getPhoneNumber().isEmpty()) {
                return true;
            }
        }


        return false;
    }

    @Override
    public boolean isUserProfileComplete(User user) {
        // Kiểm tra các trường thông tin bắt buộc
        return user.getFullName() != null && !user.getFullName().isEmpty() &&
                user.getEmail() != null && !user.getEmail().isEmpty() &&
                user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty() &&
                user.getDateOfBirth() != null &&
                user.getNationality() != null && !user.getNationality().isEmpty() &&
                user.getGender() != null && !user.getGender().isEmpty();
    }

    @Override
    public void addFollower(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
        User currentUser = getCurrentUser();
        if (!company.getFollowers().contains(currentUser)) {
            company.getFollowers().add(currentUser);
            companyRepository.save(company);
        } else {
            throw new RuntimeException("User is already following this company");
        }
    }

    @Override
    public void removeFollower(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new RuntimeException("Company not found"));
        User currentUser = getCurrentUser();
        if (company.getFollowers().contains(currentUser)) {
            company.getFollowers().remove(currentUser);
            companyRepository.save(company);
        } else {
            throw new RuntimeException("User is not following this company");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Kiểm tra nếu là người dùng thông thường (User)
        User user = userRepository.findByUsername(username);
        if (user != null) {
            Set<? extends GrantedAuthority> grantedAuthorities = getPermission(user.getPermissions());
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), grantedAuthorities);
        }

        // Kiểm tra nếu là công ty (Company)
        Optional<Company> company = companyRepository.findByUsername(username);
        if (company.isPresent()) {
            Set<? extends GrantedAuthority> grantedAuthorities = getPermission(company.get().getPermissions());
            return new org.springframework.security.core.userdetails.User(company.get().getUsername(), company.get().getPassword(), grantedAuthorities);
        }

        // Nếu không tìm thấy trong cả hai bảng, ném ngoại lệ
        throw new UsernameNotFoundException("Invalid username or password.");
    }


    private Set<? extends GrantedAuthority> getPermission(Set<Permission> permissions) {
        return permissions.stream().map(permission -> new SimpleGrantedAuthority(permission.getName())).collect(Collectors.toSet());

    }

}
